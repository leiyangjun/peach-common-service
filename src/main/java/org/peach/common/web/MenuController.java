package org.peach.common.web;

import org.peach.common.mvc.web.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.peach.common.vo.MenuVO;
import org.peach.common.service.impl.MenuServiceImpl;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 继承 {@link BaseController}。
 */
@RestController
@RequestMapping("/api/cmn/menu")
@Tag(name = "Menu接口", description = "依据代码生成，可改")
public class MenuController extends BaseController<MenuVO, MenuServiceImpl> {

	public MenuController(MenuServiceImpl service) {
		super(service);
	}
}
