package org.peach.common.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
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
import org.peach.common.mybatis.model.vo.SortVO;
import org.peach.common.mybatis.service.BaseAbstractService;
import org.peach.common.service.RoleService;
import org.peach.common.utils.BeanUtil;
import org.peach.common.utils.UserContext;
import org.peach.common.utils.TreeUtil;
import org.peach.common.vo.MenuVO;
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
	public List<MenuVO> getMenusByUserId() {
		List<MenuVO> lastResult=null;
		SortVO sortVO=new SortVO();
		sortVO.setSortName("sortNo");
		sortVO.setSortName("ASC");
		Long userId = UserContext.getUserId();
		String userName = UserContext.getUsername(); // 超级管理员时，查询所有有效菜单，并按序号进行排序
		if (ADMIN_USER.equals(userName)) {
			List<Menu> menus = menuMapper.selectByLambda(LambdaSelect.of(Menu.class).valid().sort(null));
			lastResult= TreeUtil.tree(BeanUtil.copyList(menus, MenuVO.class), MenuVO.class);
		} else { 
			// 其他的按角色去查询通过UserId--》roleId--》roleIdButtons
			List<RoleUser> roleUserList =
				roleUserMapper.selectByLambda(LambdaSelect.of(RoleUser.class).eq(RoleUser::getUserId, userId));
			List<Long> roleIds=roleUserList.stream().map(RoleUser::getRoleId).collect(Collectors.toList());
			List<RoleButton> roleBtnList=this.roleButtonMapper.selectByLambda(LambdaSelect.of(RoleButton.class).in(RoleButton::getRoleId, roleIds));
			List<Long> buttonIds=roleBtnList.stream().map(RoleButton::getButtonId).collect(Collectors.toList());
			List<MenuButton> menuButtons= menuButtonMapper.selectByLambda(LambdaSelect.of(MenuButton.class).in(MenuButton::getId, buttonIds));
			
			List<Long> menuIds=menuButtons.stream().map(MenuButton::getMenuId).collect(Collectors.toList());
			List<Menu> menus = menuMapper.selectByLambda(LambdaSelect.of(Menu.class).valid().in(Menu::getId, menuIds).sort(null));
			
			lastResult= TreeUtil.tree(BeanUtil.copyList(menus, MenuVO.class), MenuVO.class);
		}
		return lastResult;
	}
}
