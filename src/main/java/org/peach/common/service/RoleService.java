package org.peach.common.service;

import java.util.List;
import org.peach.common.mybatis.service.BaseInterfaceService;
import org.peach.common.vo.MenuTreeRoleVO;
import org.peach.common.vo.MenuTreeUserVO;
import org.peach.common.vo.RoleUserVO;
import org.peach.common.vo.RoleVO;
import org.peach.common.vo.UserVO;

/**
 * 角色管理：分页、详情、持久化、物理删除、用户绑定。
 */
public interface RoleService extends BaseInterfaceService<RoleVO> {

	/**
	 * 物理删除角色及其菜单/按钮/用户关联行。
	 *
	 * @author leiyangjun
	 */
	void deleteRoleById(Long id);

	/**
	 * 查询当前绑定在该角色下的用户主键列表（顺序稳定，便于前端初始化勾选）。
	 *
	 * @author leiyangjun
	 */
	List<UserVO> getUserByRoleId(Long roleId);

	/**
	 * 全量替换角色下的用户绑定。
	 *
	 * @author leiyangjun
	 */
	void bindUser(Long roleId, List<RoleUserVO> users);

	/**
	 * 
	 * @Title: getMenusByUserId
	 * @Description: 获取当前用户有效菜单树能够查看的
	 * @param: @return
	 * @return: List<MenuVO>
	 * @throws
	 */
	List<MenuTreeUserVO> getMenusByUserId();

	/**
	 * 
	 * @Title: getMenusByUserId
	 * @Description: 点击绑定菜单获取数据
	 * @param: @return
	 * @return: List<MenuVO>
	 * @throws
	 */
	List<MenuTreeRoleVO> getMenusByRoleId(Long roleId);

	void saveRoleMenuButton(Long roleId, List<MenuTreeRoleVO> menuTreeRoleVO);

	/**
	 * 
	 * @Title: getButtonsByMenuId
	 * @Description: 获取当前用户当前点击菜单能够显示的按钮的
	 * @param: @param menuId
	 * @param: @return
	 * @return: List<String>
	 * @throws
	 */
	List<String> getButtonsByMenuId(Long menuId);
}
