package org.peach.common.service;

import java.util.List;
import org.peach.common.mybatis.service.BaseInterfaceService;
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
}
