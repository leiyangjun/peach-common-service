package org.peach.common.web;

import java.io.Serializable;
import java.util.List;

import org.peach.common.mvc.api.context.annotation.AdminApi;
import org.peach.common.mvc.result.ApiResult;
import org.peach.common.mvc.web.BaseController;
import org.peach.common.service.MenuService;
import org.peach.common.vo.MenuVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
 * 基础路径由 {@code /api/cmn/menu} 调整为 {@code /menu}；在 {@code peach.api.context} 开启时由
 * {@link AdminApi} 固定为管理端形态，完整对外路径为 {@code /admin/menu/**}，与 peach-admin-web 的
 * {@code /api-common} + 管理前缀 + {@code /menu/...} 一致。
 * </p>
 * <p>
 * 切勿误标 {@code @AppApi}，否则将注册到 {@code /app/menu/**}，前端仍请求 {@code /admin/menu/**} 会导致 404。
 * </p>
 */
@RestController
@RequestMapping("/menu")
@Tag(name = "Menu接口", description = "菜单 CRUD 与树查询")
public class MenuController extends BaseController<MenuVO, MenuService> {

	public MenuController(MenuService service) {
		super(service);
	}

	/**
	 * 覆盖基类：保存后返回完整 {@link MenuVO}（含树字段可为空），便于前端一次拿到主键与类型；若请求体含
	 * {@code buttonBindings} 则与菜单同事务写入按钮及 API。
 *
 * @author leiyangjun
 */
	@Override
	@Operation(summary = "保存或更新菜单", description = "可选 buttonBindings：非 null 时与菜单同事务全量覆盖按钮及 API")
	@PostMapping
	public ApiResult<Serializable> save(@Valid @RequestBody MenuVO body) {
		Serializable id = service.save(body);
		return ApiResult.ok(service.getById(id));
	}

	@Operation(summary = "菜单树（有效）", description = "仅 valid=1 的记录，组装为树")
	@GetMapping("/tree")
	public ApiResult<List<MenuVO>> treeValid() {
		return ApiResult.ok(service.listMenuTreeValid());
	}

	@Operation(summary = "菜单树（全部）", description = "不按有效标记过滤，组装为树；菜单管理后台使用")
	@GetMapping("/tree/all")
	public ApiResult<List<MenuVO>> treeAll() {
		return ApiResult.ok(service.listMenuTreeAll());
	}

	@Operation(summary = "物理删除菜单", description = "按主键删除记录；若仍存在子菜单则拒绝删除")
	@DeleteMapping("/{id}")
	public ApiResult<Void> deletePhysically(
			@Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH) @PathVariable Long id) {
		service.deletePhysically(id);
		return ApiResult.ok();
	}
}
