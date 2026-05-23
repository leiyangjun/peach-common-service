package org.peach.common.web;

import java.util.List;
import org.peach.common.mvc.result.ApiResult;
import org.peach.common.mvc.web.BaseController;
import org.peach.common.service.RoleService;
import org.peach.common.vo.RoleUserVO;
import org.peach.common.vo.RoleVO;
import org.peach.common.vo.UserVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 角色管理 REST：分页、详情、保存（返回主键）、物理删除、用户绑定。
 * <p>
 * 基础路径 {@code /role}，与 {@code peach-admin-web} 经网关访问的 {@code /admin/role/**} 一致。
 * </p>
 *
 * @author leiyangjun
 */
@RestController
@RequestMapping("/role")
@Tag(name = "角色管理", description = "分页、保存、物理删除、绑定用户")
public class RoleController extends BaseController<RoleVO, RoleService> {

	public RoleController(RoleService service) {
		super(service);
	}

	@Operation(summary = "物理删除角色", description = "删除角色行及关联的菜单/按钮/用户绑定数据")
	@DeleteMapping("/{id}")
	public ApiResult<Void> deleteRoleById(
		@Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable Long id) {
		service.deleteRoleById(id);
		return ApiResult.ok();
	}

	@Operation(summary = "获取该角色下所有用户列表", description = "用于管理端初始化多选状态")
	@GetMapping("/{roleId}/user")
	public ApiResult<List<UserVO>> listUserIds(
		@Parameter(name = "roleId", required = true, in = ParameterIn.PATH) @PathVariable Long roleId) {
		return ApiResult.ok(service.getUserByRoleId(roleId));
	}

	@Operation(summary = "全量绑定用户", description = "请求体 userIds 为空数组表示清空绑定")
	@PutMapping("/{roleId}/users")
	public ApiResult<Void> replaceUsers(
		@Parameter(name = "roleId", required = true, in = ParameterIn.PATH) @PathVariable Long roleId,
		@Valid @RequestBody List<RoleUserVO> users) {
		service.bindUser(roleId, users);
		return ApiResult.ok();
	}
}
