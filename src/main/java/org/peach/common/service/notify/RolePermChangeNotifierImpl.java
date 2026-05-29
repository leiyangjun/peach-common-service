package org.peach.common.service.notify;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.peach.common.mvc.redis.RedisAccessor;
import org.peach.common.mvc.redis.RedisMessagePublisher;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * 角色权限变更后：全量重建 ROLE_USERS / ROLE_APIS 快照并 Pub/Sub 通知网关刷新本地缓存。
 * <p>
 * 平台约定：{@link RedisAccessor#increment} revision → {@link RedisAccessor#setValue} 全量快照 →
 * {@link RedisMessagePublisher#publish} 对象 revision 通知下游拉快照（与 {@link UnauthApiChangeNotifierImpl} 一致）。
 * </p>
 *
 * @author leiyangjun
 * @date 2026-05-29
 */
@Slf4j
@Component
public class RolePermChangeNotifierImpl implements RolePermChangeNotifier {

	/** 角色-用户快照；经 RedisAccessor 写入后为 COMM-{profile}-ROLE_USERS */
	private static final String BIZ_ROLE_USERS = "ROLE_USERS";

	/** 角色-用户 revision；经 RedisAccessor 写入后为 COMM-{profile}-ROLE_USERS_VERSION */
	private static final String BIZ_ROLE_USERS_VERSION = "ROLE_USERS_VERSION";

	/** 角色-API 快照；经 RedisAccessor 写入后为 COMM-{profile}-ROLE_APIS */
	private static final String BIZ_ROLE_APIS = "ROLE_APIS";

	/** 角色-API revision；经 RedisAccessor 写入后为 COMM-{profile}-ROLE_APIS_VERSION */
	private static final String BIZ_ROLE_APIS_VERSION = "ROLE_APIS_VERSION";

	private final RolePermSnapshotBuilder snapshotBuilder;

	private final RedisAccessor redisAccessor;

	private final RedisMessagePublisher messagePublisher;

	public RolePermChangeNotifierImpl(RolePermSnapshotBuilder snapshotBuilder, RedisAccessor redisAccessor,
			RedisMessagePublisher messagePublisher) {
		this.snapshotBuilder = snapshotBuilder;
		this.redisAccessor = redisAccessor;
		this.messagePublisher = messagePublisher;
	}

	@Override
	public void afterChange() {
		try {
			String updatedAt = Instant.now().toString();
			publishUsersSnapshot(updatedAt);
			publishApisSnapshot(updatedAt);
		}
		catch (Exception ex) {
			log.error("角色权限快照发布失败", ex);
		}
	}

	private void publishUsersSnapshot(String updatedAt) {
		Map<String, List<Long>> roles = snapshotBuilder.buildRoleUsers();
		Long revision = redisAccessor.increment(BIZ_ROLE_USERS_VERSION);
		long rev = revision == null ? 0L : revision;
		RolePermUsersSnapshot snapshot = new RolePermUsersSnapshot(rev, updatedAt, Map.copyOf(roles));
		redisAccessor.setValue(BIZ_ROLE_USERS, snapshot);
		messagePublisher.publish(BIZ_ROLE_USERS, rev);
		log.info("角色-用户快照已发布 revision={} roles={} redisKey={}", rev, roles.size(), BIZ_ROLE_USERS);
	}

	private void publishApisSnapshot(String updatedAt) {
		Map<String, List<RolePermApiItem>> roles = snapshotBuilder.buildRoleApis();
		Long revision = redisAccessor.increment(BIZ_ROLE_APIS_VERSION);
		long rev = revision == null ? 0L : revision;
		RolePermApisSnapshot snapshot = new RolePermApisSnapshot(rev, updatedAt, Map.copyOf(roles));
		redisAccessor.setValue(BIZ_ROLE_APIS, snapshot);
		messagePublisher.publish(BIZ_ROLE_APIS, rev);
		int apiCount = roles.values().stream().mapToInt(List::size).sum();
		log.info("角色-API 快照已发布 revision={} roles={} apis={} redisKey={}", rev, roles.size(), apiCount,
				BIZ_ROLE_APIS);
	}
}
