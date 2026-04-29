package org.peach.common.web;

import org.peach.common.mvc.web.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.peach.common.vo.MenuButtonVO;
import org.peach.common.service.impl.MenuButtonServiceImpl;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 继承 {@link BaseController}。
 */
@RestController
@RequestMapping("/api/cmn/menu/button")
@Tag(name = "CmnMenuButton接口", description = "依据代码生成，可改")
public class MenuButtonController extends BaseController<MenuButtonVO, MenuButtonServiceImpl> {

	public MenuButtonController(MenuButtonServiceImpl service) {
		super(service);
	}
}

