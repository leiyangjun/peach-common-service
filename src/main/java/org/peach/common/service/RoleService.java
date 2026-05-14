package org.peach.common.service;

import java.util.List;
import org.peach.common.dto.BindRoleUsersDTO;
import org.peach.common.vo.RoleVO;
import org.peach.common.mybatis.service.BaseInterfaceService;

/**
 * 角色管理：分页、详情、持久化、物理删除、用户绑定。
 */
public interface RoleService extends BaseInterfaceService<RoleVO> {

	/**
	 * 新增或修改：{@code id == null} 或 {@code id <= 0} 为新增；返回主键。
	 */
	Long persist(RoleVO vo);

	/**
	 * 物理删除角色及其菜单/按钮/用户关联行。
	 */
	void hardDelete(Long id);

	/**
	 * 查询当前绑定在该角色下的用户主键列表（顺序稳定，便于前端初始化勾选）。
	 */
	List<Long> listUserIds(Long roleId);

	/**
	 * 全量替换角色下的用户绑定。
	 */
	void replaceRoleUsers(Long roleId, BindRoleUsersDTO dto);
}
