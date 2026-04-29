package org.peach.common.web;

import org.peach.common.mvc.web.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.peach.common.vo.RoleVO;
import org.peach.common.service.impl.RoleServiceImpl;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 继承 {@link BaseController}。
 */
@RestController
@RequestMapping("/api/cmn/role")
@Tag(name = "CmnRole接口", description = "依据代码生成，可改")
public class RoleController extends BaseController<RoleVO, RoleServiceImpl> {

	public RoleController(RoleServiceImpl service) {
		super(service);
	}
}

