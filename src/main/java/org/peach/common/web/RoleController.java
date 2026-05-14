package org.peach.common.web;

import java.util.List;

import org.peach.common.dto.BindRoleUsersDTO;
import org.peach.common.mvc.result.ApiResult;
import org.peach.common.mvc.web.BaseController;
import org.peach.common.service.impl.RoleServiceImpl;
import org.peach.common.vo.RoleVO;
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
 */
@RestController
@RequestMapping("/role")
@Tag(name = "角色管理", description = "分页、保存、物理删除、绑定用户")
public class RoleController extends BaseController<RoleVO, RoleServiceImpl> {

	public RoleController(RoleServiceImpl service) {
		super(service);
	}

	@Operation(summary = "物理删除角色", description = "删除角色行及关联的菜单/按钮/用户绑定数据")
	@DeleteMapping("/{id}/hard")
	public ApiResult<Void> hardDelete(
			@Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable Long id) {
		service.hardDelete(id);
		return ApiResult.ok();
	}

	@Operation(summary = "查询角色已绑定用户主键列表", description = "用于管理端初始化多选状态")
	@GetMapping("/{id}/user-ids")
	public ApiResult<List<Long>> listUserIds(
			@Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable Long id) {
		return ApiResult.ok(service.listUserIds(id));
	}

	@Operation(summary = "全量替换角色下的用户绑定", description = "请求体 userIds 为空数组表示清空绑定")
	@PutMapping("/{id}/users")
	public ApiResult<Void> replaceUsers(
			@Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable Long id,
			@Valid @RequestBody BindRoleUsersDTO body) {
		service.replaceRoleUsers(id, body);
		return ApiResult.ok();
	}
}
