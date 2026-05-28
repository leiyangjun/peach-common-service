package org.peach.common.service.notify;

import java.time.Instant;
import java.util.List;
import org.peach.common.entity.UnauthApi;
import org.peach.common.mapper.UnauthApiMapper;
import org.peach.common.mvc.redis.RedisAccessor;
import org.peach.common.mybatis.lambda.LambdaSelect;
import org.peach.common.utils.BeanUtil;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * 免鉴权 API 变更后：全量重建 Redis 快照并 Pub/Sub 通知网关刷新本地缓存。
 */
@Slf4j
@Component
public class UnauthApiChangeNotifierImpl implements UnauthApiChangeNotifier {

	/** 全量快照 JSON；Pub/Sub 刷新频道与之为同一业务 Key */
	public static final String BIZ_SNAPSHOT = "UNAUTHAPI";

	/** 单调递增 revision（INCR） */
	public static final String BIZ_REVISION = "UNAUTHAPIREV";

	private final UnauthApiMapper mapper;

	private final RedisAccessor redisAccessor;

	public UnauthApiChangeNotifierImpl(UnauthApiMapper mapper, RedisAccessor redisAccessor) {
		this.mapper = mapper;
		this.redisAccessor = redisAccessor;
	}

	@Override
	public void afterChange() {
		rebuildAndPublish();
	}

	/**
	 * 从库表全量重建快照写入 Redis，并发布刷新消息。
	 */
	public void rebuildAndPublish() {
		try {
			List<UnauthApi> rows = mapper.selectByLambda(LambdaSelect.of(UnauthApi.class).valid());
			List<UnauthApiItem> items = BeanUtil.copyList(rows, UnauthApiItem.class);
			Long revision = redisAccessor.increment(BIZ_REVISION);
			long rev = revision == null ? 0L : revision;
			UnauthApiSnapshot snapshot = new UnauthApiSnapshot(rev, Instant.now().toString(), List.copyOf(items));
			redisAccessor.setObject(BIZ_SNAPSHOT, snapshot);
			redisAccessor.publish(BIZ_SNAPSHOT, Long.toString(rev));
			log.info("免鉴权 API 快照已发布 revision={} items={} redisKey={}", rev, items.size(), BIZ_SNAPSHOT);
		} catch (Exception ex) {
			log.error("免鉴权 API 快照发布失败", ex);
		}
	}
}
