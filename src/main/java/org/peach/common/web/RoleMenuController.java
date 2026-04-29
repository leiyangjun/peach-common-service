package org.peach.common.web;

import org.peach.common.mvc.web.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.peach.common.vo.RoleMenuVO;
import org.peach.common.service.impl.RoleMenuServiceImpl;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 继承 {@link BaseController}。
 */
@RestController
@RequestMapping("/api/cmn/role/menu")
@Tag(name = "CmnRoleMenu接口", description = "依据代码生成，可改")
public class RoleMenuController extends BaseController<RoleMenuVO, RoleMenuServiceImpl> {

	public RoleMenuController(RoleMenuServiceImpl service) {
		super(service);
	}
}

