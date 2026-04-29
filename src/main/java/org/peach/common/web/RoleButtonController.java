package org.peach.common.web;

import org.peach.common.mvc.web.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.peach.common.vo.RoleButtonVO;
import org.peach.common.service.impl.RoleButtonServiceImpl;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 继承 {@link BaseController}。
 */
@RestController
@RequestMapping("/api/cmn/role/button")
@Tag(name = "CmnRoleButton接口", description = "依据代码生成，可改")
public class RoleButtonController extends BaseController<RoleButtonVO, RoleButtonServiceImpl> {

	public RoleButtonController(RoleButtonServiceImpl service) {
		super(service);
	}
}

