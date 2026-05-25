package org.peach.common.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.peach.common.entity.Button;
import org.peach.common.entity.Menu;
import org.peach.common.entity.MenuButton;
import org.peach.common.entity.RoleButton;
import org.peach.common.entity.RoleUser;
import org.peach.common.mapper.ButtonMapper;
import org.peach.common.mapper.MenuButtonMapper;
import org.peach.common.mapper.MenuMapper;
import org.peach.common.mapper.RoleButtonMapper;
import org.peach.common.mapper.RoleMapper;
import org.peach.common.mapper.RoleUserMapper;
import org.peach.common.mapper.UserMapper;
import org.peach.common.mybatis.lambda.LambdaDelete;
import org.peach.common.mybatis.lambda.LambdaSelect;
import org.peach.common.utils.UserContext;
import org.peach.common.vo.MenuButtonRoleVO;
import org.peach.common.vo.MenuTreeRoleVO;
import org.peach.common.vo.MenuTreeUserVO;
import org.peach.common.vo.RoleUserVO;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * {@link RoleServiceImpl} 角色菜单授权与用户菜单树单测（Mockito，不启 Spring 容器）。
 *
 * @author leiyangjun
 * @date 2026-05-26
 */
@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

	@Mock
	private RoleMapper roleMapper;

	@Mock
	private RoleUserMapper roleUserMapper;

	@Mock
	private RoleButtonMapper roleButtonMapper;

	@Mock
	private UserMapper userMapper;

	@Mock
	private MenuMapper menuMapper;

	@Mock
	private MenuButtonMapper menuButtonMapper;

	@Mock
	private ButtonMapper buttonMapper;

	@InjectMocks
	private RoleServiceImpl roleService;

	@BeforeEach
	void resetRequestContext() {
		RequestContextHolder.resetRequestAttributes();
	}

	@AfterEach
	void tearDownRequestContext() {
		RequestContextHolder.resetRequestAttributes();
	}

	@Test
	void getMenusByRoleId_marksGrantedButtonsAndBuildsTree() {
		Long roleId = 1L;
		Menu root = menu(1L, 0L, "ROOT", 1);
		Menu child = menu(2L, 1L, "CHILD", 2);
		MenuButton mb = menuButton(2L, 100L, "BTN_ADD");
		RoleButton granted = roleButton(roleId, 2L, 100L);

		when(menuMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(List.of(root, child));
		when(menuButtonMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(List.of(mb));
		when(roleButtonMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(List.of(granted));

		List<MenuTreeRoleVO> tree = roleService.getMenusByRoleId(roleId);

		assertThat(tree).hasSize(1);
		MenuTreeRoleVO rootVo = tree.get(0);
		assertThat(rootVo.getId()).isEqualTo(1L);
		assertThat(rootVo.getChildren()).hasSize(1);
		MenuTreeRoleVO childVo = rootVo.getChildren().get(0);
		assertThat(childVo.getButtonRoleVOs()).hasSize(1);
		MenuButtonRoleVO br = childVo.getButtonRoleVOs().get(0);
		assertThat(br.getRoleId()).isEqualTo(roleId);
		assertThat(br.getPermission()).isTrue();
		assertThat(br.getButtonId()).isEqualTo(100L);
	}

	@Test
	void getMenusByRoleId_ungrantedButtonPermissionFalse() {
		Long roleId = 9L;
		Menu leaf = menu(10L, 0L, "LEAF", 1);
		MenuButton mb = menuButton(10L, 200L, "BTN_DEL");
		when(menuMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(List.of(leaf));
		when(menuButtonMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(List.of(mb));
		when(roleButtonMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(List.of());

		List<MenuTreeRoleVO> tree = roleService.getMenusByRoleId(roleId);

		assertThat(tree).hasSize(1);
		assertThat(tree.get(0).getButtonRoleVOs().get(0).getPermission()).isFalse();
	}

	@Test
	void saveRoleMenuButton_emptyList_onlyDeletesBindings() {
		Long roleId = 5L;
		roleService.saveRoleMenuButton(roleId, List.of());
		verify(roleButtonMapper).deleteByLambda(any(LambdaDelete.class));
		verify(roleButtonMapper, never()).batchInsertBase(any());
	}

	@Test
	void saveRoleMenuButton_flatList_insertsOnlyGrantedWithRoleId() {
		Long roleId = 7L;
		MenuButtonRoleVO granted = vo(1L, 10L, true);
		MenuButtonRoleVO denied = vo(1L, 11L, false);
		MenuButtonRoleVO granted2 = vo(2L, 20L, true);

		roleService.saveRoleMenuButton(roleId, List.of(granted, denied, granted2));

		verify(roleButtonMapper).deleteByLambda(any(LambdaDelete.class));
		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<RoleButton>> captor = ArgumentCaptor.forClass(List.class);
		verify(roleButtonMapper).batchInsertBase(captor.capture());
		List<RoleButton> inserted = captor.getValue();
		assertThat(inserted).hasSize(2);
		assertThat(inserted).allMatch(rb -> roleId.equals(rb.getRoleId()));
		assertThat(inserted).extracting(RoleButton::getButtonId).containsExactlyInAnyOrder(10L, 20L);
	}

	@Test
	void saveRoleMenuButton_allDenied_noBatchInsert() {
		Long roleId = 8L;
		roleService.saveRoleMenuButton(roleId, List.of(vo(1L, 1L, false), vo(1L, 2L, false)));
		verify(roleButtonMapper).deleteByLambda(any(LambdaDelete.class));
		verify(roleButtonMapper, never()).batchInsertBase(any());
	}

	@Test
	void getMenusByUserId_admin_returnsAllValidMenus() {
		mockUserContext(1L, "admin");
		Menu m1 = menu(100L, 0L, "A", 1);
		Menu m2 = menu(101L, 100L, "B", 2);
		when(menuMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(List.of(m1, m2));

		List<MenuTreeUserVO> tree = roleService.getMenusByUserId();

		assertThat(tree).hasSize(1);
		assertThat(tree.get(0).getId()).isEqualTo(100L);
		assertThat(tree.get(0).isAdmin()).isTrue();
		assertThat(tree.get(0).getChildren()).hasSize(1);
		verify(roleUserMapper, never()).selectByLambda(any(LambdaSelect.class));
	}

	@Test
	void getMenusByUserId_normalUser_aggregatesRoleButtonsIntoTree() {
		mockUserContext(42L, "bob");
		RoleUser ru = new RoleUser();
		ru.setUserId(42L);
		ru.setRoleId(10L);
		RoleButton rb = roleButton(10L, 200L, 300L);
		Button btn = new Button();
		btn.setId(300L);
		btn.setButtonCode("QUERY");
		btn.setButtonName("查询");
		Menu menu = menu(200L, 0L, "USER_MGMT", 1);

		when(roleUserMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(List.of(ru));
		when(roleButtonMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(List.of(rb));
		when(buttonMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(List.of(btn));
		when(menuMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(List.of(menu));

		List<MenuTreeUserVO> tree = roleService.getMenusByUserId();

		assertThat(tree).hasSize(1);
		MenuTreeUserVO node = tree.get(0);
		assertThat(node.isAdmin()).isFalse();
		assertThat(node.getButtons()).hasSize(1);
		assertThat(node.getButtons().get(0).getButtonCode()).isEqualTo("QUERY");
	}

	@Test
	void bindUser_emptyList_onlyClearsOldBindings() {
		Long roleId = 3L;
		roleService.bindUser(roleId, List.of());
		verify(roleUserMapper).deleteByLambda(any(LambdaDelete.class));
		verify(roleUserMapper, never()).batchInsertBase(any());
	}

	@Test
	void bindUser_replacesRoleUsers() {
		Long roleId = 4L;
		RoleUserVO vo = new RoleUserVO();
		vo.setRoleId(roleId);
		vo.setUserId(99L);
		roleService.bindUser(roleId, List.of(vo));
		verify(roleUserMapper).deleteByLambda(any(LambdaDelete.class));
		verify(roleUserMapper).batchInsertBase(any());
	}

	@Test
	void deleteRoleById_cascadesPhysicalDeletes() {
		Long roleId = 6L;
		roleService.deleteRoleById(roleId);
		verify(roleMapper).deleteBaseByKey(roleId, org.peach.common.entity.Role.class);
		verify(roleUserMapper).deleteByLambda(any(LambdaDelete.class));
		verify(roleButtonMapper).deleteByLambda(any(LambdaDelete.class));
	}

	private static Menu menu(Long id, Long parentId, String code, int orderNo) {
		Menu m = new Menu();
		m.setId(id);
		m.setParentId(parentId);
		m.setMenuCode(code);
		m.setMenuName(code);
		m.setOrderNo(orderNo);
		m.setValid((short) 1);
		return m;
	}

	private static MenuButton menuButton(Long menuId, Long buttonId, String code) {
		MenuButton mb = new MenuButton();
		mb.setMenuId(menuId);
		mb.setButtonId(buttonId);
		mb.setButtonCode(code);
		mb.setButtonName(code);
		return mb;
	}

	private static RoleButton roleButton(Long roleId, Long menuId, Long buttonId) {
		RoleButton rb = new RoleButton();
		rb.setRoleId(roleId);
		rb.setMenuId(menuId);
		rb.setButtonId(buttonId);
		return rb;
	}

	private static MenuButtonRoleVO vo(Long menuId, Long buttonId, boolean permission) {
		MenuButtonRoleVO vo = new MenuButtonRoleVO();
		vo.setMenuId(menuId);
		vo.setButtonId(buttonId);
		vo.setPermission(permission);
		return vo;
	}

	private static void mockUserContext(Long userId, String username) {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.setParameter(UserContext.QUERY_USER_ID, String.valueOf(userId));
		req.setParameter(UserContext.QUERY_USERNAME, username);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(req));
	}
}
