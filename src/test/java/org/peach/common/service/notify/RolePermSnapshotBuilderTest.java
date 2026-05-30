package org.peach.common.service.notify;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.peach.common.entity.ButtonApi;
import org.peach.common.entity.Role;
import org.peach.common.entity.RoleButton;
import org.peach.common.entity.RoleUser;
import org.peach.common.mapper.ButtonApiMapper;
import org.peach.common.mapper.RoleButtonMapper;
import org.peach.common.mapper.RoleMapper;
import org.peach.common.mapper.RoleUserMapper;

/**
 * {@link RoleSnapshotBuilder} 物化逻辑单测。
 *
 * @author leiyangjun
 * @date 2026-05-29
 */
@ExtendWith(MockitoExtension.class)
class RolePermSnapshotBuilderTest {

	@Mock
	private RoleMapper roleMapper;

	@Mock
	private RoleUserMapper roleUserMapper;

	@Mock
	private RoleButtonMapper roleButtonMapper;

	@Mock
	private ButtonApiMapper buttonApiMapper;

	private RoleSnapshotBuilder builder;

	@BeforeEach
	void setUp() {
		builder = new RoleSnapshotBuilder(roleMapper, roleUserMapper, roleButtonMapper, buttonApiMapper);
	}

	@Test
	void buildRoleUsersGroupsByRoleCode() {
		Role role = new Role();
		role.setId(1L);
		role.setRoleCode("ROLE_ADMIN");
		when(roleMapper.selectByLambda(any())).thenReturn(List.of(role));

		RoleUser ru1 = new RoleUser();
		ru1.setRoleId(1L);
		ru1.setUserId(100L);
		RoleUser ru2 = new RoleUser();
		ru2.setRoleId(1L);
		ru2.setUserId(200L);
		when(roleUserMapper.selectByLambda(any())).thenReturn(List.of(ru1, ru2));

		var result = builder.buildRoleUsers();
		assertThat(result).containsKey("ROLE_ADMIN");
		assertThat(result.get("ROLE_ADMIN")).containsExactly(100L, 200L);
	}

	@Test
	void buildRoleApisMaterializesFromRoleButtonAndButtonApi() {
		Role role = new Role();
		role.setId(1L);
		role.setRoleCode("ROLE_OPS");
		when(roleMapper.selectByLambda(any())).thenReturn(List.of(role));

		RoleButton rb = new RoleButton();
		rb.setRoleId(1L);
		rb.setMenuId(10L);
		rb.setButtonId(20L);
		when(roleButtonMapper.selectByLambda(any())).thenReturn(List.of(rb));

		ButtonApi api = new ButtonApi();
		api.setMenuId(10L);
		api.setButtonId(20L);
		api.setMethod("post");
		api.setServiceName("peach-common-service");
		api.setUrlPath("/admin/user/list");
		when(buttonApiMapper.selectByLambda(any())).thenReturn(List.of(api));

		var result = builder.buildRoleApis();
		assertThat(result).containsKey("ROLE_OPS");
		assertThat(result.get("ROLE_OPS")).hasSize(1);
		RoleApiItem item = result.get("ROLE_OPS").get(0);
		assertThat(item.getMethod()).isEqualTo("POST");
		assertThat(item.getFinalPath()).isEqualTo("/peach-common-service/admin/user/list");
	}
}
