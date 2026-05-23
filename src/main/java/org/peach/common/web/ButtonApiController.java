package org.peach.common.web;

import org.peach.common.mvc.web.BaseController;
import org.peach.common.service.ButtonApiService;
import org.peach.common.vo.ButtonApiVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 继承 {@link BaseController}。
 *
 * @author leiyangjun
 */
@RestController
@RequestMapping("/button")
@Tag(name = "Button接口，前端暂时没有提供按钮管理", description = "依据代码生成，可改")
public class ButtonApiController extends BaseController<ButtonApiVO, ButtonApiService> {

	public ButtonApiController(ButtonApiService service) {
		super(service);
	}
}
