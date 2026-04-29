package org.peach.common.web;

import org.peach.common.mvc.web.BaseController;
import org.peach.common.service.impl.MenuServiceImpl;
import org.peach.common.vo.MenuVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 继承 {@link BaseController}。
 */
@RestController
@RequestMapping("/menu")
@Tag(name = "菜单相关", description = "菜单管理")
public class MenuController extends BaseController<MenuVO, MenuServiceImpl> {

	public MenuController(MenuServiceImpl service) {
		super(service);
	}
}

