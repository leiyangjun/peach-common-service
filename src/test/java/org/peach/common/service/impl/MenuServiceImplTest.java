package org.peach.common.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.peach.common.entity.ButtonApi;
import org.peach.common.entity.Menu;
import org.peach.common.entity.MenuButton;
import org.peach.common.mapper.ButtonApiMapper;
import org.peach.common.mapper.MenuButtonMapper;
import org.peach.common.mapper.MenuMapper;
import org.peach.common.mapper.RoleButtonMapper;
import org.peach.common.mybatis.lambda.LambdaDelete;
import org.peach.common.mybatis.lambda.LambdaSelect;
import org.peach.common.vo.ButtonApiVO;
import org.peach.common.vo.MenuButtonInfoVO;
import org.peach.common.vo.MenuButtonVO;
import org.peach.common.vo.MenuInfoVO;
import org.peach.common.vo.MenuVO;

/**
 * {@link MenuServiceImpl} 菜单保存与详情查询单测。
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

	@InjectMocks
	private MenuServiceImpl menuService;

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
	void deleteMenuById_cascadesRelatedRows() {
		Long menuId = 90L;
		menuService.deleteMenuById(menuId);
		verify(menuMapper).deleteBaseByKey(menuId, Menu.class);
		verify(menuButtonMapper).deleteByLambda(any(LambdaDelete.class));
		verify(buttonApiMapper).deleteByLambda(any(LambdaDelete.class));
		verify(roleButtonMapper).deleteByLambda(any(LambdaDelete.class));
	}
}
