package org.peach.common.service.notify;

import java.time.Instant;
import java.util.List;
import org.peach.common.entity.UnauthApi;
import org.peach.common.mapper.UnauthApiMapper;
import org.peach.common.mvc.redis.RedisAccessor;
import org.peach.common.mvc.redis.RedisMessagePublisher;
import org.peach.common.mybatis.lambda.LambdaSelect;
import org.peach.common.utils.BeanUtil;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * 免鉴权 API 变更后：全量重建 Redis 快照并 Pub/Sub 通知网关刷新本地缓存。
 * <p>
 * 平台约定：{@link RedisAccessor#increment} revision → {@link RedisAccessor#setValue} 全量快照 →
 * {@link RedisMessagePublisher#publish} 对象 revision 通知下游拉快照（与 start JavaDoc 方式二一致）。
 * </p>
 */
@Slf4j
@Component
public class UnauthApiChangeNotifierImpl implements UnauthApiChangeNotifier {

	/** 全量快照业务 Key；经 RedisAccessor 写入后为 COMM-ACTIVE-UNAUTHAPI，Pub/Sub 频道同名 */
	private static final String BIZ_SNAPSHOT = "UNAUTHAPI";

	/** 单调递增 revision 业务 Key（INCR）；经 RedisAccessor 写入后为 COMM-ACTIVE-UNAUTHAPI_VERSION */
	private static final String BIZ_REVISION = "UNAUTHAPI_VERSION";

	private final UnauthApiMapper mapper;

	private final RedisAccessor redisAccessor;

	private final RedisMessagePublisher messagePublisher;

	public UnauthApiChangeNotifierImpl(UnauthApiMapper mapper, RedisAccessor redisAccessor,
			RedisMessagePublisher messagePublisher) {
		this.mapper = mapper;
		this.redisAccessor = redisAccessor;
		this.messagePublisher = messagePublisher;
	}

	@Override
	public void afterChange() {
		try {
			List<UnauthApi> rows = mapper.selectByLambda(LambdaSelect.of(UnauthApi.class).valid());
			List<UnauthApiItem> items = BeanUtil.copyList(rows, UnauthApiItem.class);
			Long revision = redisAccessor.increment(BIZ_REVISION);
			long rev = revision == null ? 0L : revision;
			UnauthApiSnapshot snapshot = new UnauthApiSnapshot(rev, Instant.now().toString(), List.copyOf(items));
			redisAccessor.setValue(BIZ_SNAPSHOT, snapshot);
			messagePublisher.publish(BIZ_SNAPSHOT, rev);
			log.info("免鉴权 API 快照已发布 revision={} items={} redisKey={}", rev, items.size(), BIZ_SNAPSHOT);
		}
		catch (Exception ex) {
			log.error("免鉴权 API 快照发布失败", ex);
		}
	}
}
