package org.peach.common.web;

import org.peach.common.mvc.web.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.peach.common.vo.ButtonApiVO;
import org.peach.common.service.impl.ButtonApiServiceImpl;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 继承 {@link BaseController}。
 */
@RestController
@RequestMapping("/api/cmn/button/api")
@Tag(name = "CmnButtonApi接口", description = "依据代码生成，可改")
public class ButtonApiController extends BaseController<ButtonApiVO, ButtonApiServiceImpl> {

	public ButtonApiController(ButtonApiServiceImpl service) {
		super(service);
	}
}

