package org.peach.common.web;

import java.util.List;
import org.peach.common.mvc.result.ApiResult;
import org.peach.common.mvc.web.BaseController;
import org.peach.common.service.ButtonService;
import org.peach.common.vo.ButtonVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 继承 {@link BaseController}。
 *
 * @author leiyangjun
 */
@RestController
@RequestMapping("/button")
@Tag(name = "Button接口，前端暂时没有提供按钮管理", description = "依据代码生成，可改")
public class ButtonController extends BaseController<ButtonVO, ButtonService> {

	public ButtonController(ButtonService service) {
		super(service);
	}

	@Operation(summary = "条件分页查询（GET）",
		description = "分页与排序：pageNum、pageSize、sortName、sortType；与 VO 类型 V 同名的参数作为等值条件；searchValue 为关键字模糊。与 GET .../{id} 路径不冲突。")
	@GetMapping("/all")
	public ApiResult<List<ButtonVO>> getButtonList() {
		return ApiResult.ok(service.getButtonList());
	}
}
