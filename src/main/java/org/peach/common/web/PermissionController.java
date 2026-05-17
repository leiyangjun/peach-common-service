package org.peach.common.web;

import java.util.List;

import org.peach.common.dto.MenuButtonApiReplaceDTO;
import org.peach.common.dto.MenuButtonReplaceDTO;
import org.peach.common.dto.RoleMenuButtonReplaceDTO;
import org.peach.common.entity.ButtonDict;
import org.peach.common.mvc.result.ApiResult;
import org.peach.common.mvc.util.ApiMeta;
import org.peach.common.service.PermissionFacadeService;
import org.peach.common.vo.MenuButtonPickerRowVO;
import org.peach.common.vo.RegistryServiceItemVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 权限配置聚合：菜单按钮字典、按钮 API、角色菜单按钮、注册服务列表。
 * <p>
 * 对外完整路径在开启 {@code peach.api.context} 时为 {@code /admin/permission/**}（与 peach-admin-web
 * {@code /api-common} + 管理前缀一致）。
 * </p>
 *
 * @author leiyangjun
 */
@RestController
@RequestMapping("/permission")
@Tag(name = "权限配置", description = "菜单按钮、API 绑定、角色按钮、服务发现")
@RequiredArgsConstructor
public class PermissionController {

	private final PermissionFacadeService permissionFacadeService;

	@Operation(summary = "全局按钮字典列表")
	@GetMapping("/button-dict")
	public ApiResult<List<ButtonDict>> listButtonDict() {
		return ApiResult.ok(permissionFacadeService.listButtonDict());
	}

	@Operation(summary = "菜单已绑定按钮（含字典主键）")
	@GetMapping("/menu/{menuId}/buttons")
	public ApiResult<List<MenuButtonPickerRowVO>> listMenuButtons(@PathVariable Long menuId) {
		return ApiResult.ok(permissionFacadeService.listMenuButtonBindRows(menuId));
	}

	@Operation(summary = "全量覆盖菜单按钮绑定", description = "仅允许 cmn_button 主键；MENU 类型自动包含 BTN_VIEW。推荐改用 POST /menu 携带 buttonBindings。", deprecated = true)
	@PutMapping("/menu/{menuId}/buttons")
	public ApiResult<Void> replaceMenuButtons(@PathVariable Long menuId, @RequestBody MenuButtonReplaceDTO body) {
		permissionFacadeService.replaceMenuButtons(menuId, body);
		return ApiResult.ok();
	}

	@Operation(summary = "注册中心服务列表（下拉）")
	@GetMapping("/registry/services")
	public ApiResult<List<RegistryServiceItemVO>> listServices() {
		return ApiResult.ok(permissionFacadeService.listRegistryServices());
	}

	@Operation(summary = "菜单按钮已绑定的 API 列表", description = "字段与 ApiMeta 对齐，供前端表格回显")
	@GetMapping("/menu-button/{menuButtonId}/apis")
	public ApiResult<List<ApiMeta>> listMenuButtonApis(@PathVariable Long menuButtonId) {
		return ApiResult.ok(permissionFacadeService.listMenuButtonApis(menuButtonId));
	}

	@Operation(summary = "全量覆盖菜单按钮上的 API 绑定", description = "推荐改用 POST /menu 携带 buttonBindings。", deprecated = true)
	@PutMapping("/menu-button/{menuButtonId}/apis")
	public ApiResult<Void> replaceMenuButtonApis(@PathVariable Long menuButtonId,
			@RequestBody MenuButtonApiReplaceDTO body) {
		permissionFacadeService.replaceMenuButtonApis(menuButtonId, body);
		return ApiResult.ok();
	}

	@Operation(summary = "角色已绑定的菜单按钮实例 id")
	@GetMapping("/role/{roleId}/menu-button-ids")
	public ApiResult<List<Long>> listRoleMenuButtonIds(@PathVariable Long roleId) {
		return ApiResult.ok(permissionFacadeService.listRoleMenuButtonIds(roleId));
	}

	@Operation(summary = "角色绑定菜单按钮（全量覆盖）")
	@PutMapping("/role/{roleId}/menu-buttons")
	public ApiResult<Void> replaceRoleMenuButtons(@PathVariable Long roleId,
			@RequestBody RoleMenuButtonReplaceDTO body) {
		permissionFacadeService.replaceRoleMenuButtons(roleId, body);
		return ApiResult.ok();
	}

	@Operation(summary = "全部菜单按钮实例（角色绑定选择器）")
	@GetMapping("/menu-buttons/role-picker")
	public ApiResult<List<MenuButtonPickerRowVO>> listMenuButtonsForRolePicker() {
		return ApiResult.ok(permissionFacadeService.listAllMenuButtonsForRolePicker());
	}
}
