package org.peach.common.web;

import java.util.List;
import org.peach.common.mvc.api.context.annotation.AdminApi;
import org.peach.common.mvc.result.ApiResult;
import org.peach.common.mvc.web.BaseController;
import org.peach.common.service.MenuService;
import org.peach.common.vo.MenuInfoVO;
import org.peach.common.vo.MenuOpsPatchVO;
import org.peach.common.vo.MenuTreeVO;
import org.peach.common.vo.MenuVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
 * 继承 {@link BaseController}；补充菜单树（不分页）接口。
 * <p>
 * 基础路径由 {@code /api/cmn/menu} 调整为 {@code /menu}；在 {@code peach.api.context} 开启时由 {@link AdminApi} 固定为管理端形态，完整对外路径为
 * {@code /admin/menu/**}，与 peach-admin-web 的 {@code /api-common} + 管理前缀 + {@code /menu/...} 一致。
 * </p>
 * <p>
 * 切勿误标 {@code @AppApi}，否则将注册到 {@code /app/menu/**}，前端仍请求 {@code /admin/menu/**} 会导致 404。
 * </p>
 */
@RestController
@RequestMapping("/menu")
@Tag(name = "Menu接口", description = "菜单 CRUD 与树查询")
public class MenuController {

	private final MenuService service;

	public MenuController(MenuService service) {
		this.service = service;
	}

	@Operation(summary = "保存或更新菜单", description = "可选 buttonBindings：非 null 时与菜单同事务全量覆盖按钮及 API")
	@PostMapping
	public ApiResult<Void> save(@Valid @RequestBody MenuInfoVO menuSaveVO) {
		service.saveOrUpdate(menuSaveVO);
		return ApiResult.ok();
	}

	@Operation(summary = "调整菜单父级菜单--用于菜单管理", description = "拖拽调整菜单父级菜单")
	@PutMapping("/{menuId}/{parentId}")
	public ApiResult<List<MenuTreeVO>> editParentId(
		@Parameter(name = "menuId", description = "菜单的主键ID", required = true,
			in = ParameterIn.PATH) @PathVariable("menuId") Long menuId,
		@Parameter(name = "parentId", description = "父级菜单ID，一级为 0", required = true,
			in = ParameterIn.PATH) @PathVariable("parentId") Long parentId) {
		service.editParentId(menuId, parentId);
		return ApiResult.ok(service.getMenuTreeAll());
	}

	@Operation(summary = "菜单树（全部）--用于菜单管理", description = "组装为树；菜单管理后台使用")
	@GetMapping("/tree/all")
	public ApiResult<List<MenuTreeVO>> getMenuTreeAll() {
		return ApiResult.ok(service.getMenuTreeAll());
	}

	@Operation(summary = "根据菜单ID获取菜单详情", description = "按主键删除记录；若仍存在子菜单则拒绝删除")
	@GetMapping("/{menuId}")
	public ApiResult<MenuInfoVO> getMenuId(@Parameter(name = "menuId", description = "菜单的主键ID", required = true,
		in = ParameterIn.PATH) @PathVariable("menuId") Long id) {
		return ApiResult.ok(service.getMenuInfoById(id));
	}

	@Operation(summary = "物理删除菜单", description = "按主键删除记录；若仍存在子菜单则拒绝删除")
	@DeleteMapping("/{menuId}")
	public ApiResult<Void> delete(@Parameter(name = "menuId", description = "菜单的主键ID", required = true,
		in = ParameterIn.PATH) @PathVariable("menuId") Long id) {
		service.deleteMenuById(id);
		return ApiResult.ok();
	}

	@Operation(summary = "运维菜单详情", description = "CATALOG/MENU；不含按钮/API 绑定区")
	@GetMapping("/ops/{menuId}")
	public ApiResult<MenuVO> getMenuOps(@Parameter(name = "menuId", description = "菜单主键", required = true,
		in = ParameterIn.PATH) @PathVariable("menuId") Long menuId) {
		return ApiResult.ok(service.getMenuOpsDetail(menuId));
	}

	@Operation(summary = "新建运维目录", description = "仅 CATALOG；服务端生成 menuCode/routePath；自动绑定 BTN_QUERY")
	@PostMapping("/ops")
	public ApiResult<Void> createMenuOps(@Valid @RequestBody MenuVO menuVO) {
		service.createMenuOpsCatalog(menuVO);
		return ApiResult.ok();
	}

	@Operation(summary = "物理删除运维目录", description = "仅 CATALOG、无子节点；MENU 不可删")
	@DeleteMapping("/ops/{menuId}")
	public ApiResult<Void> deleteMenuOps(@Parameter(name = "menuId", description = "目录主键", required = true,
		in = ParameterIn.PATH) @PathVariable("menuId") Long menuId) {
		service.deleteMenuOpsById(menuId);
		return ApiResult.ok();
	}

	@Operation(summary = "更新运维菜单", description = "可改名称/排序/上级/图标/显示/备注；类型不可改；不删除已有 menu_button")
	@PatchMapping("/ops/{menuId}")
	public ApiResult<Void> patchMenuOps(@Parameter(name = "menuId", description = "目录主键", required = true,
		in = ParameterIn.PATH) @PathVariable("menuId") Long menuId, @Valid @RequestBody MenuOpsPatchVO patchVO) {
		service.patchMenuOps(menuId, patchVO);
		return ApiResult.ok();
	}
}
