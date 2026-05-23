package org.peach.common.web;

import java.util.List;
import org.peach.common.mvc.api.context.annotation.AppApi;
import org.peach.common.mvc.result.ApiResult;
import org.peach.common.service.DictService;
import org.peach.common.vo.DictVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 码表配置 REST：分页条件使用 {@link DictPageQuery}；行数据与保存体为 {@link DictVO}；启用/停用仅改 {@code status}，物理删除走 hard。
 *
 * @author leiyangjun
 */
@AppApi
@RestController
@RequestMapping("/dict")
@Tag(name = "APP端配置表查询", description = "APP端配置表查询")
public class DictAppController {

	private final DictService service;

	public DictAppController(DictService service) {
		this.service = service;
	}

	@Operation(summary = "根据字典类型获取有效字典数据--供前端使用选项数据匹配label类似")
	@GetMapping("/{dictType}")
	public ApiResult<List<DictVO>> getByDictType(
		@Parameter(name = "dictType", required = true, in = ParameterIn.PATH) @PathVariable String dictType) {
		return ApiResult.ok(service.getByDictType(dictType));
	}
}
