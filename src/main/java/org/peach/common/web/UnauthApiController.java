package org.peach.common.web;

import org.peach.common.mvc.result.ApiResult;
import org.peach.common.mvc.web.BaseController;
import org.peach.common.service.UnauthApiService;
import org.peach.common.vo.UnauthApiVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 网关免鉴权 API 管理：继承 {@link BaseController} 的分页、详情与保存；补充启停切换与物理删除。
 * <p>
 * 分页等值条件与 {@link UnauthApiVO} 同名查询参数绑定（如 {@code valid}、{@code isExternal}）；
 * 关键字模糊使用 {@code searchValue}。
 * </p>
 */
@RestController
@RequestMapping("/unauth-api")
@Tag(name = "免鉴权 API", description = "网关 JWT 白名单动态配置；变更后写入 Redis 并通知网关刷新")
public class UnauthApiController extends BaseController<UnauthApiVO, UnauthApiService> {

	public UnauthApiController(UnauthApiService service) {
		super(service);
	}

	@Operation(summary = "切换启用状态", description = "valid 在 0 与 1 之间切换")
	@PostMapping("/{id}/toggle-valid")
	public ApiResult<Short> toggleValid(
			@Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable Long id) {
		return ApiResult.ok(service.toggleValid(id));
	}

	@Operation(summary = "物理删除", description = "deletable=0 时不允许删除")
	@DeleteMapping("/{id}")
	public ApiResult<Void> deleteById(
			@Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable Long id) {
		service.deleteById(id);
		return ApiResult.ok();
	}
}
