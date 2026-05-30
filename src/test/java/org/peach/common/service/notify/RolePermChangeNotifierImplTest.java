package org.peach.common.service.notify;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.peach.common.mvc.redis.RedisAccessor;
import org.peach.common.mvc.redis.RedisMessagePublisher;

/**
 * {@link RoleChangeNotifierImpl} 发布流程单测。
 *
 * @author leiyangjun
 * @date 2026-05-29
 */
@ExtendWith(MockitoExtension.class)
class RolePermChangeNotifierImplTest {

	@Mock
	private RoleSnapshotBuilder snapshotBuilder;

	@Mock
	private RedisAccessor redisAccessor;

	@Mock
	private RedisMessagePublisher messagePublisher;

	private RoleChangeNotifierImpl notifier;

	@BeforeEach
	void setUp() {
		notifier = new RoleChangeNotifierImpl(snapshotBuilder, redisAccessor, messagePublisher);
	}

	@Test
	void afterChangeIncrementsVersionsAndPublishesBothSnapshots() {
		when(snapshotBuilder.buildRoleUsers()).thenReturn(Map.of("ROLE_ADMIN", List.of(1L)));
		when(snapshotBuilder.buildRoleApis()).thenReturn(Map.of("ROLE_ADMIN",
				List.of(new RoleApiItem("GET", "/peach-common-service/admin/user"))));
		when(redisAccessor.increment("ROLE_USERS_VERSION")).thenReturn(3L);
		when(redisAccessor.increment("ROLE_APIS_VERSION")).thenReturn(4L);

		notifier.afterChange();

		verify(redisAccessor).setValue(eq("ROLE_USERS"), org.mockito.ArgumentMatchers.any(RoleUsersSnapshot.class));
		verify(redisAccessor).setValue(eq("ROLE_APIS"), org.mockito.ArgumentMatchers.any(RoleApisSnapshot.class));
		verify(messagePublisher).publish("ROLE_USERS", 3L);
		verify(messagePublisher).publish("ROLE_APIS", 4L);
	}
}
