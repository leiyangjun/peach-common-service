package org.peach.common.web;

import org.peach.common.mvc.result.ApiResult;
import org.peach.common.mvc.web.BaseController;
import org.peach.common.service.ApplicationService;
import org.peach.common.vo.ApplicationVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 继承 {@link BaseController}，注入 Service 接口。
 *
 * @author leiyangjun
 */
@RestController
@RequestMapping("/application")
@Tag(name = "Application应用管理接口", description = "依据代码生成，可改")
public class ApplicationController extends BaseController<ApplicationVO, ApplicationService> {

	public ApplicationController(ApplicationService service) {
		super(service);
	}

	@Operation(summary = "根据主键查询详情", description = "按路径参数主键查询单条记录；无记录时由统一响应封装与异常处理决定返回内容。")
	@DeleteMapping("/{id}")
	public ApiResult<Void> deleteById(@Parameter(name = "id", description = "主键值（数值型，与表 bigint 一致）", required = true,
		in = ParameterIn.PATH) @PathVariable Long id) {
		service.deleteAppById(id);
		return ApiResult.ok();
	}

}
