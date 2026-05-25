package org.peach.common.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.peach.common.code.BizMessageCode;
import org.peach.common.entity.ButtonApi;
import org.peach.common.entity.Menu;
import org.peach.common.entity.MenuButton;
import org.peach.common.entity.RoleButton;
import org.peach.common.mapper.ButtonApiMapper;
import org.peach.common.mapper.MenuButtonMapper;
import org.peach.common.mapper.MenuMapper;
import org.peach.common.mapper.RoleButtonMapper;
import org.peach.common.mvc.exception.BizException;
import org.peach.common.mybatis.lambda.LambdaDelete;
import org.peach.common.mybatis.lambda.LambdaSelect;
import org.peach.common.mybatis.model.vo.SortVO;
import org.peach.common.mybatis.service.BaseAbstractService;
import org.peach.common.service.MenuService;
import org.peach.common.utils.BeanUtil;
import org.peach.common.utils.TreeUtil;
import org.peach.common.vo.ButtonApiVO;
import org.peach.common.vo.MenuButtonInfoVO;
import org.peach.common.vo.MenuButtonVO;
import org.peach.common.vo.MenuInfoVO;
import org.peach.common.vo.MenuTreeVO;
import org.peach.common.vo.MenuVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 菜单业务实现：树形列表（不分页）及保存前父子关系校验；树结构由 {@link TreeUtil} 组装。
 */
@Service
public class MenuServiceImpl extends BaseAbstractService<MenuMapper, Menu, MenuVO> implements MenuService {

	private final MenuButtonMapper menuButtonMapper;

	private final ButtonApiMapper buttonApiMapper;

	private final RoleButtonMapper roleButtonMapper;

	protected MenuServiceImpl(MenuMapper mapper, MenuButtonMapper menuButtonMapper, ButtonApiMapper buttonApiMapper,
		RoleButtonMapper roleButtonMapper) {
		super(mapper, Menu.class, MenuVO.class);
		this.menuButtonMapper = menuButtonMapper;
		this.buttonApiMapper = buttonApiMapper;
		this.roleButtonMapper = roleButtonMapper;
	}

	@Override
	@Transactional
	public void saveOrUpdate(MenuInfoVO menuInfoVO) {
		Menu menu = BeanUtil.copy(menuInfoVO.getMenu(), Menu.class);
		// 第一步保存菜单
		this.mapper.saveOrUpdate(menu);
		// 先删除原有的按钮-->删除绑定API信息，因为你无法确认用户反反复复在页面操作几次
		this.menuButtonMapper.deleteByLambda(LambdaDelete.of(MenuButton.class).eq(MenuButton::getMenuId, menu.getId()));
		this.buttonApiMapper.deleteByLambda(LambdaDelete.of(ButtonApi.class).eq(ButtonApi::getMenuId, menu.getId()));

		// menuButtons 为 null 表示仅更新菜单主表，不触碰按钮与 API 绑定（与 POST /menu 文档约定一致）
		if (menuInfoVO.getMenuButtons() == null) {
			return;
		}

		List<ButtonApi> buttonApis = new ArrayList<ButtonApi>();
		List<Long> buttonIds = new ArrayList<Long>();// 得到所有按钮ID，角色需要删除多余的按钮绑定
		// 第二补保存按钮信息
		List<MenuButton> menuButtons = menuInfoVO.getMenuButtons().stream().map(e -> {
			if (e.getMenuButton().getMenuId() == null) {
				e.getMenuButton().setMenuId(menu.getId());
			}
			buttonApis.addAll(BeanUtil.copyList(e.getButtonApis(), ButtonApi.class));
			buttonIds.add(e.getMenuButton().getButtonId());
			return BeanUtil.copy(e.getMenuButton(), MenuButton.class);
		}).collect(Collectors.toList());
		// 第三步保存按钮api信息

		// 还要删除角色绑定过的多余的按钮信息，比如原来绑定角色按钮A，现在按钮A没有绑定到该菜单了
		LambdaDelete<RoleButton> lambdaDelete =
			LambdaDelete.of(RoleButton.class).eq(RoleButton::getMenuId, menu.getId());
		if (!CollectionUtils.isEmpty(buttonIds)) {
			lambdaDelete.notIn(RoleButton::getButtonId, buttonIds);
		}
		buttonApis.stream().forEach(e -> e.setMenuId(menu.getId()));
		this.roleButtonMapper.deleteByLambda(lambdaDelete);
		if (!CollectionUtils.isEmpty(menuButtons)) {
			this.menuButtonMapper.batchInsertBase(menuButtons);
		}
		if (!CollectionUtils.isEmpty(buttonApis)) {
			this.buttonApiMapper.batchInsertBase(buttonApis);
		}
	}

	@Override
	@Transactional
	public void deleteMenuById(Long menuId) {
		List<Menu> childMenu = this.mapper.selectByLambda(LambdaSelect.of(Menu.class).eq(Menu::getParentId, menuId));
		if (!CollectionUtils.isEmpty(childMenu)) {
			throw BizException.validWarn(BizMessageCode.Menu.MENU_HAS_CHILDREN);
		}
		this.mapper.deleteBaseByKey(menuId, Menu.class);
		// 同步删除该菜单关联权限 删除菜单绑定按钮--》按钮绑定API
		this.menuButtonMapper.deleteByLambda(LambdaDelete.of(MenuButton.class).eq(MenuButton::getMenuId, menuId));
		this.buttonApiMapper.deleteByLambda(LambdaDelete.of(ButtonApi.class).eq(ButtonApi::getMenuId, menuId));
		// 删除该菜单角色绑定
		this.roleButtonMapper.deleteByLambda(LambdaDelete.of(RoleButton.class).eq(RoleButton::getMenuId, menuId));
	}

	@Override
	public List<MenuTreeVO> getMenuTreeAll() {
		SortVO sortVO = new SortVO();
		sortVO.setSortName("orderNo");
		sortVO.setSortName("ASC");
		List<Menu> menus = mapper.selectBaseAll(new Menu(), sortVO);
		return TreeUtil.tree(BeanUtil.copyList(menus, MenuTreeVO.class), MenuTreeVO.class);
	}

	@Override
	public MenuInfoVO getMenuInfoById(Long menuId) {
		MenuInfoVO infoVO = new MenuInfoVO();
		Menu menu = this.mapper.selectBaseByKey(menuId, Menu.class);
		infoVO.setMenu(BeanUtil.copy(menu, MenuVO.class));
		// 拿到绑定按钮信息
		List<MenuButton> menuButtons =
			menuButtonMapper.selectByLambda(LambdaSelect.of(MenuButton.class).eq(MenuButton::getMenuId, menuId));
		List<ButtonApi> buttonApis =
			buttonApiMapper.selectByLambda(LambdaSelect.of(ButtonApi.class).eq(ButtonApi::getMenuId, menuId));
		// List<MenuButtonVO> menuButtonVOs = BeanUtil.copyList(menuButtons, MenuButtonVO.class);

		Map<Long, List<ButtonApi>> map = buttonApis.stream().collect(Collectors.groupingBy(ButtonApi::getButtonId));

		List<MenuButtonInfoVO> menuButtonInfoVOs = menuButtons.stream().map(e -> {
			MenuButtonInfoVO buttonInfoVO = new MenuButtonInfoVO();
			buttonInfoVO.setMenuButton(BeanUtil.copy(e, MenuButtonVO.class));
			buttonInfoVO.setButtonApis(BeanUtil.copyList(map.get(e.getButtonId()), ButtonApiVO.class));
			return buttonInfoVO;
		}).collect(Collectors.toList());
		infoVO.setMenuButtons(menuButtonInfoVOs);
		return infoVO;
	}
}
