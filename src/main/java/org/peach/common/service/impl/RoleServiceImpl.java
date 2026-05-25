package org.peach.common.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.peach.common.entity.Button;
import org.peach.common.entity.Menu;
import org.peach.common.entity.MenuButton;
import org.peach.common.entity.Role;
import org.peach.common.entity.RoleButton;
import org.peach.common.entity.RoleUser;
import org.peach.common.entity.User;
import org.peach.common.mapper.ButtonMapper;
import org.peach.common.mapper.MenuButtonMapper;
import org.peach.common.mapper.MenuMapper;
import org.peach.common.mapper.RoleButtonMapper;
import org.peach.common.mapper.RoleMapper;
import org.peach.common.mapper.RoleUserMapper;
import org.peach.common.mapper.UserMapper;
import org.peach.common.mybatis.lambda.LambdaDelete;
import org.peach.common.mybatis.lambda.LambdaSelect;
import org.peach.common.mybatis.service.BaseAbstractService;
import org.peach.common.service.RoleService;
import org.peach.common.utils.BeanUtil;
import org.peach.common.utils.TreeUtil;
import org.peach.common.utils.UserContext;
import org.peach.common.vo.ButtonUserVO;
import org.peach.common.vo.MenuButtonRoleVO;
import org.peach.common.vo.MenuTreeRoleVO;
import org.peach.common.vo.MenuTreeUserVO;
import org.peach.common.vo.RoleUserVO;
import org.peach.common.vo.RoleVO;
import org.peach.common.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 角色管理：关键字分页、编码唯一校验、物理删级联清理、用户绑定全量替换。
 *
 * @author leiyangjun
 */
@Service
public class RoleServiceImpl extends BaseAbstractService<RoleMapper, Role, RoleVO> implements RoleService {

	/**
	 * 定义管理员内置超级账户，避免删除
	 */
	private final static String ADMIN_USER = "admin";

	private final RoleUserMapper roleUserMapper;
	private final RoleButtonMapper roleButtonMapper;
	private final UserMapper userMapper;
	private final MenuMapper menuMapper;
	private final MenuButtonMapper menuButtonMapper;
	private final ButtonMapper buttonMapper;

	@Override
	public Serializable save(RoleVO vo) {
		return this.mapper.saveOrUpdate(BeanUtil.copy(vo, Role.class));
	}

	public RoleServiceImpl(RoleMapper mapper, RoleUserMapper roleUserMapper, RoleButtonMapper roleButtonMapper,
		UserMapper userMapper, MenuMapper menuMapper, MenuButtonMapper menuButtonMapper, ButtonMapper buttonMapper) {
		super(mapper, Role.class, RoleVO.class);
		this.roleUserMapper = roleUserMapper;
		this.roleButtonMapper = roleButtonMapper;
		this.userMapper = userMapper;
		this.menuMapper = menuMapper;
		this.menuButtonMapper = menuButtonMapper;
		this.buttonMapper = buttonMapper;
	}

	@Override
	@Transactional
	public void deleteRoleById(Long id) {
		// 物理删除角色，包含该角色下用户信息以及绑定的角色按钮菜单信息
		this.mapper.deleteBaseByKey(id, Role.class);
		this.roleUserMapper.deleteByLambda(LambdaDelete.of(RoleUser.class).eq(RoleUser::getRoleId, id));
		this.roleButtonMapper.deleteByLambda(LambdaDelete.of(RoleButton.class).eq(RoleButton::getRoleId, id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserVO> getUserByRoleId(Long roleId) {
		// 获取该角色下的用户信息，先拿到角色用户表信息--》用户ID--》用户信息
		List<RoleUser> roleUsers =
			this.roleUserMapper.selectByLambda(LambdaSelect.of(RoleUser.class).eq(RoleUser::getRoleId, roleId));
		List<Long> userIds = roleUsers.stream().map(RoleUser::getUserId).collect(Collectors.toList());
		List<User> result = null;
		if (!CollectionUtils.isEmpty(userIds)) {
			result = userMapper.selectBaseByKeys(userIds, User.class, null);
		}
		return BeanUtil.copyList(result, UserVO.class);
	}

	@Override
	@Transactional
	public void bindUser(Long roleId, List<RoleUserVO> users) {
		this.roleUserMapper.deleteByLambda(LambdaDelete.of(RoleUser.class).eq(RoleUser::getRoleId, roleId));
		if (!CollectionUtils.isEmpty(users)) {
			this.roleUserMapper.batchInsertBase(BeanUtil.copyList(users, RoleUser.class));
		}
	}

	@Override
	public List<String> getButtonsByMenuId(Long menuId) {
		// UserContext.getUserId();
		List<MenuButton> buttonList =
			menuButtonMapper.selectByLambda(LambdaSelect.of(MenuButton.class).eq(MenuButton::getMenuId, menuId));
		List<String> buttonIds = buttonList.stream().map(MenuButton::getButtonCode).collect(Collectors.toList());
		return buttonIds;
	}

	@Override
	public List<MenuTreeUserVO> getMenusByUserId() {
		Long userId = UserContext.getUserId();
		String userName = UserContext.getUsername(); // 超级管理员时，查询所有有效菜单，并按序号进行排序

		List<MenuTreeUserVO> resultList = new ArrayList<MenuTreeUserVO>();
		if (ADMIN_USER.equals(userName)) {
			List<Menu> menus =
				menuMapper.selectByLambda(LambdaSelect.of(Menu.class).valid().orderByAsc(Menu::getOrderNo));
			resultList = BeanUtil.copyList(menus, MenuTreeUserVO.class);
		} else {
			// 其他的按角色去查询通过UserId--》roleId--》roleIdButtons
			List<RoleUser> roleUserList =
				roleUserMapper.selectByLambda(LambdaSelect.of(RoleUser.class).eq(RoleUser::getUserId, userId));
			List<Long> roleIds = roleUserList.stream().map(RoleUser::getRoleId).collect(Collectors.toList());
			List<RoleButton> roleBtnList = this.roleButtonMapper
				.selectByLambda(LambdaSelect.of(RoleButton.class).in(RoleButton::getRoleId, roleIds));
			// 获取所有角色按钮信息，查询出所有的按钮信息
			List<Long> buttonIds = roleBtnList.stream().map(RoleButton::getButtonId).collect(Collectors.toList());
			List<Button> buttons =
				buttonMapper.selectByLambda(LambdaSelect.of(Button.class).in(Button::getId, buttonIds));
			Map<Long, Button> mapButton =
				buttons.stream().collect(Collectors.toMap(Button::getId, Function.identity()));

			List<Long> menuIds = roleBtnList.stream().map(RoleButton::getMenuId).collect(Collectors.toList());
			// 原始菜单信息
			List<Menu> menus = menuMapper.selectByLambda(
				LambdaSelect.of(Menu.class).valid().in(Menu::getId, menuIds).orderByAsc(Menu::getOrderNo));

			Map<Long, Menu> mapMenu = menus.stream().collect(Collectors.toMap(Menu::getId, Function.identity()));

			Map<Long, List<Long>> menuButtonIdMap =
				roleBtnList.stream().collect(Collectors.groupingBy(RoleButton::getMenuId, // 按 menuId 分组
					Collectors.mapping(RoleButton::getButtonId, Collectors.toList()) // 将 buttonId 收集成 List
				));
			resultList = mapMenu.values().stream().map(menu -> {
				MenuTreeUserVO vo = new MenuTreeUserVO();
				// 拷贝菜单属性（可使用 BeanUtils 或手动 set）
				BeanUtil.copyProperties(menu, vo); // 需确保 Menu 与 MenuButtonVO 字段名匹配
				// 获取当前菜单下的按钮 ID 列表，若无则空列表
				List<Long> buttonIdTemps = menuButtonIdMap.getOrDefault(menu.getId(), Collections.emptyList());
				// 根据 ID 列表从 mapButton 中取出按钮（过滤掉不存在的 ID）
				List<Button> buttonTemps = buttonIdTemps.stream().map(mapButton::get).collect(Collectors.toList());
				vo.setButtons(BeanUtil.copyList(buttonTemps, ButtonUserVO.class));
				return vo;
			}).collect(Collectors.toList());
		}
		resultList.stream().forEach(e -> e.setAdmin(ADMIN_USER.equals(userName)));
		return TreeUtil.tree(resultList, MenuTreeUserVO.class);
	}

	@Override
	public void saveRoleMenuButton(Long roleId, List<MenuTreeRoleVO> menuTreeRoleVOs) {
		List<MenuTreeRoleVO> tempVOs = TreeUtil.flatten(menuTreeRoleVOs, MenuTreeRoleVO.class, true);
		List<MenuButtonRoleVO> roleButtons = new ArrayList<MenuButtonRoleVO>();
		tempVOs.stream().forEach(e -> roleButtons.addAll(e.getButtonRoleVOs()));

		List<MenuButtonRoleVO> lastRoleButtons =
			roleButtons.stream().filter(e -> e.getPermission()).collect(Collectors.toList());

		// 先删除角色下的权限
		this.roleButtonMapper.deleteByLambda(LambdaDelete.of(RoleButton.class).eq(RoleButton::getRoleId, roleId));
		if (!CollectionUtils.isEmpty(lastRoleButtons)) {
			this.roleButtonMapper.batchInsertBase(BeanUtil.copyList(lastRoleButtons, RoleButton.class));
		}
	}

	@Override
	public List<MenuTreeRoleVO> getMenusByRoleId(Long roleId) {
		// 第一步得到所有的有效菜单

		// 得到所有的菜单对应按钮信息
		List<Menu> menus = menuMapper.selectByLambda(LambdaSelect.of(Menu.class).valid().orderByAsc(Menu::getOrderNo));

		List<Long> menuIds = menus.stream().map(Menu::getId).collect(Collectors.toList());

		// 得到所有按钮
		List<MenuButton> menuButtons =
			menuButtonMapper.selectByLambda(LambdaSelect.of(MenuButton.class).in(MenuButton::getMenuId, menuIds));
		List<MenuButtonRoleVO> buttonRoleVOs = BeanUtil.copyList(menuButtons, MenuButtonRoleVO.class);

		// 得到选中的按钮
		List<RoleButton> roleButtons =
			roleButtonMapper.selectByLambda(LambdaSelect.of(RoleButton.class).eq(RoleButton::getRoleId, roleId));
		Map<String, List<RoleButton>> map =
			roleButtons.stream().collect(Collectors.groupingBy(dto -> dto.getMenuId() + ":" + dto.getButtonId()));
		buttonRoleVOs.stream().forEach(brVO -> {
			brVO.setRoleId(roleId);
			if (map.containsKey(brVO.getMenuId() + ":" + brVO.getButtonId())) {
				brVO.setPermission(Boolean.TRUE);
			} else {
				brVO.setPermission(Boolean.FALSE);
			}
		});
		// 将所有的菜单按钮根据 菜单ID分组得到Map<menuID,List<MenuButton>>
		List<MenuTreeRoleVO> menuTreeRoleVOs = BeanUtil.copyList(menus, MenuTreeRoleVO.class);

		Map<Long, List<MenuButtonRoleVO>> mapTemp =
			buttonRoleVOs.stream().collect(Collectors.groupingBy(MenuButtonRoleVO::getMenuId));

		menuTreeRoleVOs.stream().forEach(ee -> {
			ee.setButtonRoleVOs(mapTemp.get(ee.getId()));
		});
		return TreeUtil.tree(menuTreeRoleVOs, MenuTreeRoleVO.class);
	}
}
