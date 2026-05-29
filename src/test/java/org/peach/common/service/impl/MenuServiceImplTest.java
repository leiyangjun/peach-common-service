package org.peach.common.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.peach.common.mvc.exception.BizException;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.peach.common.entity.Button;
import org.peach.common.entity.ButtonApi;
import org.peach.common.entity.Menu;
import org.peach.common.entity.MenuButton;
import org.peach.common.mapper.ButtonApiMapper;
import org.peach.common.mapper.ButtonMapper;
import org.peach.common.mapper.MenuButtonMapper;
import org.peach.common.mapper.MenuMapper;
import org.peach.common.mapper.RoleButtonMapper;
import org.peach.common.service.notify.RolePermChangeNotifier;
import org.peach.common.mybatis.lambda.LambdaDelete;
import org.peach.common.mybatis.lambda.LambdaSelect;
import org.peach.common.mybatis.lambda.LambdaUpdate;
import org.peach.common.vo.ButtonApiVO;
import org.peach.common.vo.MenuButtonInfoVO;
import org.peach.common.vo.MenuButtonVO;
import org.peach.common.vo.MenuInfoVO;
import org.peach.common.vo.MenuOpsPatchVO;
import org.peach.common.vo.MenuVO;

/**
 * {@link MenuServiceImpl} 菜单保存与运维能力单测。
 *
 * @author leiyangjun
 * @date 2026-05-26
 */
@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {

	@Mock
	private MenuMapper menuMapper;

	@Mock
	private MenuButtonMapper menuButtonMapper;

	@Mock
	private ButtonApiMapper buttonApiMapper;

	@Mock
	private RoleButtonMapper roleButtonMapper;

	@Mock
	private ButtonMapper buttonMapper;

	@Mock
	private RolePermChangeNotifier rolePermChangeNotifier;

	@InjectMocks
	private MenuServiceImpl menuService;

	@Test
	void saveOrUpdate_persistsMenuWithoutParentValidation() {
		MenuVO menuVo = new MenuVO();
		menuVo.setMenuCode("SYS_PAGE");
		menuVo.setMenuName("页面");
		menuVo.setMenuType("MENU");
		menuVo.setParentId(0L);
		MenuInfoVO info = new MenuInfoVO();
		info.setMenu(menuVo);
		info.setMenuButtons(null);

		when(menuMapper.saveOrUpdate(any(Menu.class))).thenAnswer(inv -> {
			inv.getArgument(0, Menu.class).setId(1L);
			return 1;
		});

		menuService.saveOrUpdate(info);

		verify(menuMapper).saveOrUpdate(any(Menu.class));
		verify(menuButtonMapper).deleteByLambda(any(LambdaDelete.class));
		verify(buttonApiMapper).deleteByLambda(any(LambdaDelete.class));
	}

	@Test
	void saveOrUpdate_updateParentId_persistsViaSaveOrUpdate() {
		Long menuId = 9102L;
		Long newParentId = 300000000005L;
		MenuVO menuVo = new MenuVO();
		menuVo.setId(menuId);
		menuVo.setMenuCode("SYS_APP");
		menuVo.setMenuName("应用管理");
		menuVo.setMenuType("MENU");
		menuVo.setParentId(newParentId);
		menuVo.setRoutePath("/system/application");
		MenuInfoVO info = new MenuInfoVO();
		info.setMenu(menuVo);
		info.setMenuButtons(null);

		when(menuMapper.saveOrUpdate(any(Menu.class))).thenAnswer(inv -> {
			Menu m = inv.getArgument(0);
			m.setId(menuId);
			return 1;
		});

		menuService.saveOrUpdate(info);

		ArgumentCaptor<Menu> captor = ArgumentCaptor.forClass(Menu.class);
		verify(menuMapper).saveOrUpdate(captor.capture());
		assertThat(captor.getValue().getParentId()).isEqualTo(newParentId);
		assertThat(captor.getValue().getRoutePath()).isEqualTo("/system/application");
	}

	@Test
	void saveOrUpdate_menuButtonsNull_updatesMenuWithoutReinsertButtons() {
		Long menuId = 50L;
		MenuVO menuVo = new MenuVO();
		menuVo.setId(menuId);
		menuVo.setMenuCode("SYS_DEMO");
		menuVo.setMenuName("演示菜单");
		MenuInfoVO info = new MenuInfoVO();
		info.setMenu(menuVo);
		info.setMenuButtons(null);

		when(menuMapper.saveOrUpdate(any(Menu.class))).thenAnswer(inv -> {
			Menu m = inv.getArgument(0);
			m.setId(menuId);
			return 1;
		});

		menuService.saveOrUpdate(info);

		verify(menuMapper).saveOrUpdate(any(Menu.class));
		verify(menuButtonMapper).deleteByLambda(any(LambdaDelete.class));
		verify(buttonApiMapper).deleteByLambda(any(LambdaDelete.class));
		verify(menuButtonMapper, never()).batchInsertBase(any());
		verify(buttonApiMapper, never()).batchInsertBase(any());
		verify(roleButtonMapper, never()).deleteByLambda(any(LambdaDelete.class));
	}

	@Test
	void saveOrUpdate_withMenuButtons_replacesBindingsAndPrunesRoleButtons() {
		Long menuId = 60L;
		MenuVO menuVo = new MenuVO();
		menuVo.setId(menuId);
		MenuInfoVO info = new MenuInfoVO();
		info.setMenu(menuVo);

		MenuButtonVO mbVo = new MenuButtonVO();
		mbVo.setButtonId(700L);
		MenuButtonInfoVO row = new MenuButtonInfoVO();
		row.setMenuButton(mbVo);
		ButtonApiVO apiVo = new ButtonApiVO();
		apiVo.setButtonId(700L);
		apiVo.setUrlPath("/admin/demo");
		row.setButtonApis(List.of(apiVo));
		info.setMenuButtons(List.of(row));

		when(menuMapper.saveOrUpdate(any(Menu.class))).thenAnswer(inv -> {
			Menu m = inv.getArgument(0);
			m.setId(menuId);
			return 1;
		});

		menuService.saveOrUpdate(info);

		verify(roleButtonMapper).deleteByLambda(any(LambdaDelete.class));
		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<MenuButton>> mbCaptor = ArgumentCaptor.forClass(List.class);
		verify(menuButtonMapper).batchInsertBase(mbCaptor.capture());
		assertThat(mbCaptor.getValue()).hasSize(1);
		assertThat(mbCaptor.getValue().get(0).getMenuId()).isEqualTo(menuId);
		assertThat(mbCaptor.getValue().get(0).getButtonId()).isEqualTo(700L);

		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<ButtonApi>> apiCaptor = ArgumentCaptor.forClass(List.class);
		verify(buttonApiMapper).batchInsertBase(apiCaptor.capture());
		assertThat(apiCaptor.getValue()).hasSize(1);
		assertThat(apiCaptor.getValue().get(0).getMenuId()).isEqualTo(menuId);
	}

	@Test
	void saveOrUpdate_emptyMenuButtonsList_clearsWithoutInsert() {
		Long menuId = 61L;
		MenuVO menuVo = new MenuVO();
		menuVo.setId(menuId);
		MenuInfoVO info = new MenuInfoVO();
		info.setMenu(menuVo);
		info.setMenuButtons(Collections.emptyList());

		when(menuMapper.saveOrUpdate(any(Menu.class))).thenAnswer(inv -> {
			inv.getArgument(0, Menu.class).setId(menuId);
			return 1;
		});

		menuService.saveOrUpdate(info);

		verify(menuButtonMapper).deleteByLambda(any(LambdaDelete.class));
		verify(menuButtonMapper, never()).batchInsertBase(any());
		verify(roleButtonMapper).deleteByLambda(any(LambdaDelete.class));
	}

	@Test
	void getMenuInfoById_groupsButtonApisByButtonId() {
		Long menuId = 80L;
		Menu menu = new Menu();
		menu.setId(menuId);
		menu.setMenuCode("M80");
		menu.setMenuName("菜单80");
		MenuButton mb1 = new MenuButton();
		mb1.setMenuId(menuId);
		mb1.setButtonId(1L);
		mb1.setButtonCode("ADD");
		ButtonApi api1 = new ButtonApi();
		api1.setMenuId(menuId);
		api1.setButtonId(1L);
		api1.setUrlPath("/a");
		ButtonApi api2 = new ButtonApi();
		api2.setMenuId(menuId);
		api2.setButtonId(1L);
		api2.setUrlPath("/b");

		when(menuMapper.selectBaseByKey(menuId, Menu.class)).thenReturn(menu);
		when(menuButtonMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(List.of(mb1));
		when(buttonApiMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(List.of(api1, api2));

		MenuInfoVO info = menuService.getMenuInfoById(menuId);

		assertThat(info.getMenu().getMenuCode()).isEqualTo("M80");
		assertThat(info.getMenuButtons()).hasSize(1);
		assertThat(info.getMenuButtons().get(0).getButtonApis()).hasSize(2);
	}

	@Test
	void createMenuOpsCatalog_insertsCatalogAndBtnQuery() {
		Long menuId = 9001L;
		MenuVO menuVO = new MenuVO();
		menuVO.setId(menuId);
		menuVO.setMenuType("CATALOG");
		menuVO.setMenuName("TestDir");
		menuVO.setOrderNo(20);
		menuVO.setValid((short) 1);

		Button queryBtn = new Button();
		queryBtn.setId(700000000001L);
		queryBtn.setButtonCode("BTN_QUERY");
		queryBtn.setButtonName("查询");

		when(menuMapper.saveOrUpdate(any(Menu.class))).thenAnswer(inv -> {
			Menu m = inv.getArgument(0);
			m.setId(menuId);
			return 1;
		});
		when(buttonMapper.selectOneByLambda(any(LambdaSelect.class))).thenReturn(queryBtn);

		menuService.createMenuOpsCatalog(menuVO);

		verify(menuMapper).saveOrUpdate(any(Menu.class));
		verify(menuButtonMapper).deleteByLambda(any(LambdaDelete.class));
		verify(buttonApiMapper).deleteByLambda(any(LambdaDelete.class));
		ArgumentCaptor<MenuButton> captor = ArgumentCaptor.forClass(MenuButton.class);
		verify(menuButtonMapper).insertBase(captor.capture());
		assertThat(captor.getValue().getMenuId()).isEqualTo(menuId);
		assertThat(captor.getValue().getButtonCode()).isEqualTo("BTN_QUERY");
		assertThat(captor.getValue().getButtonName()).isEqualTo("查询");
	}

	@Test
	void createMenuOpsCatalog_rejectsMenuType() {
		MenuVO menuVO = new MenuVO();
		menuVO.setMenuName("OpsPage");
		menuVO.setMenuType("MENU");

		assertThatThrownBy(() -> menuService.createMenuOpsCatalog(menuVO)).isInstanceOf(BizException.class);
		verify(menuMapper, never()).saveOrUpdate(any(Menu.class));
	}

	@Test
	void patchMenuOps_updatesWhitelistWithoutTouchingButtons() {
		Long menuId = 9100L;
		MenuOpsPatchVO patch = new MenuOpsPatchVO();
		patch.setMenuName("新名");
		patch.setOrderNo(99);
		patch.setIcon("IconTest");
		patch.setValid((short) 1);
		patch.setRemark("备注");

		menuService.patchMenuOps(menuId, patch);

		verify(menuMapper).updateByLambdaQuery(any(LambdaUpdate.class));
		verify(menuMapper, never()).saveOrUpdate(any(Menu.class));
		verify(menuButtonMapper, never()).deleteByLambda(any());
		verify(menuButtonMapper, never()).insertBase(any());
		verify(menuButtonMapper, never()).batchInsertBase(any());
	}

	@Test
	void patchMenuOps_blankRemarkUsesNullUpdate() {
		Long menuId = 9101L;
		MenuOpsPatchVO patch = new MenuOpsPatchVO();
		patch.setMenuName("测试");
		patch.setOrderNo(1);
		patch.setIcon("IconTest");
		patch.setValid((short) 1);
		patch.setRemark("   ");

		menuService.patchMenuOps(menuId, patch);

		verify(menuMapper).updateByLambdaQuery(any(LambdaUpdate.class));
	}

	@Test
	void getMenuOpsDetail_returnsMenuVo() {
		Menu menu = new Menu();
		menu.setId(9200L);
		menu.setMenuType("MENU");
		menu.setMenuCode("SYS_MENU_OPS");
		menu.setMenuName("运维菜单");
		menu.setValid((short) 1);
		when(menuMapper.selectBaseByKey(9200L, Menu.class)).thenReturn(menu);

		MenuVO detail = menuService.getMenuOpsDetail(9200L);

		assertThat(detail.getMenuType()).isEqualTo("MENU");
		assertThat(detail.getMenuName()).isEqualTo("运维菜单");
	}

	@Test
	void deleteMenuOpsById_deletesCatalogWithoutChildren() {
		Long menuId = 9202L;
		Menu catalog = new Menu();
		catalog.setId(menuId);
		catalog.setMenuType("CATALOG");
		catalog.setValid((short) 1);

		when(menuMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(Collections.emptyList());
		when(menuMapper.selectBaseByKey(menuId, Menu.class)).thenReturn(catalog);

		menuService.deleteMenuOpsById(menuId);

		verify(menuMapper).deleteBaseByKey(menuId, Menu.class);
		verify(menuButtonMapper).deleteByLambda(any(LambdaDelete.class));
		verify(buttonApiMapper).deleteByLambda(any(LambdaDelete.class));
		verify(roleButtonMapper).deleteByLambda(any(LambdaDelete.class));
	}

	@Test
	void deleteMenuOpsById_rejectsMenuType() {
		Long menuId = 9203L;
		Menu menu = new Menu();
		menu.setId(menuId);
		menu.setMenuType("MENU");
		menu.setValid((short) 1);
		when(menuMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(Collections.emptyList());
		when(menuMapper.selectBaseByKey(menuId, Menu.class)).thenReturn(menu);

		assertThatThrownBy(() -> menuService.deleteMenuOpsById(menuId)).isInstanceOf(BizException.class);
		verify(menuMapper, never()).deleteBaseByKey(menuId, Menu.class);
	}

	@Test
	void deleteMenuOpsById_rejectsWhenHasChildren() {
		Long menuId = 9204L;
		Menu catalog = new Menu();
		catalog.setId(menuId);
		catalog.setMenuType("CATALOG");
		catalog.setValid((short) 1);
		Menu child = new Menu();
		child.setId(9205L);
		child.setParentId(menuId);

		when(menuMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(List.of(child));

		assertThatThrownBy(() -> menuService.deleteMenuOpsById(menuId)).isInstanceOf(BizException.class);
		verify(menuMapper, never()).deleteBaseByKey(menuId, Menu.class);
	}

	@Test
	void editParentId_validatesCatalogParentAndUpdatesOnly() {
		Long menuId = 9102L;
		Long newParentId = 300000000005L;
		Menu newParent = new Menu();
		newParent.setId(newParentId);
		newParent.setMenuType("CATALOG");
		newParent.setValid((short) 1);

		when(menuMapper.selectBaseByKey(newParentId, Menu.class)).thenReturn(newParent);

		menuService.editParentId(menuId, newParentId);

		verify(menuMapper).selectBaseByKey(newParentId, Menu.class);
		verify(menuMapper).updateByLambdaQuery(any(LambdaUpdate.class));
		verify(menuMapper, never()).saveOrUpdate(any(Menu.class));
	}

	@Test
	void editParentId_rejectsWhenParentNotCatalog() {
		Long menuId = 9102L;
		Long parentId = 300000000004L;
		Menu parentMenu = new Menu();
		parentMenu.setId(parentId);
		parentMenu.setMenuType("MENU");
		parentMenu.setValid((short) 1);

		when(menuMapper.selectBaseByKey(parentId, Menu.class)).thenReturn(parentMenu);

		assertThatThrownBy(() -> menuService.editParentId(menuId, parentId)).isInstanceOf(BizException.class);
		verify(menuMapper, never()).updateByLambdaQuery(any(LambdaUpdate.class));
	}

	@Test
	void editParentId_rejectsWhenParentNotFound() {
		Long menuId = 9102L;
		Long parentId = 999L;

		when(menuMapper.selectBaseByKey(parentId, Menu.class)).thenReturn(null);

		assertThatThrownBy(() -> menuService.editParentId(menuId, parentId)).isInstanceOf(BizException.class);
		verify(menuMapper, never()).updateByLambdaQuery(any(LambdaUpdate.class));
	}

	@Test
	void deleteMenuById_cascadesRelatedRows() {
		Long menuId = 90L;
		when(menuMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(Collections.emptyList());

		menuService.deleteMenuById(menuId);

		verify(menuMapper).deleteBaseByKey(menuId, Menu.class);
		verify(menuButtonMapper).deleteByLambda(any(LambdaDelete.class));
		verify(buttonApiMapper).deleteByLambda(any(LambdaDelete.class));
		verify(roleButtonMapper).deleteByLambda(any(LambdaDelete.class));
	}

	@Test
	void deleteMenuById_rejectsWhenHasChildren() {
		Long menuId = 91L;
		Menu child = new Menu();
		child.setId(92L);
		child.setParentId(menuId);
		when(menuMapper.selectByLambda(any(LambdaSelect.class))).thenReturn(List.of(child));

		assertThatThrownBy(() -> menuService.deleteMenuById(menuId)).isInstanceOf(BizException.class);
		verify(menuMapper, never()).deleteBaseByKey(menuId, Menu.class);
	}
}
