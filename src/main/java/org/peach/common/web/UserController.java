package org.peach.common.web;

import org.peach.common.mvc.web.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.peach.common.vo.UserVO;
import org.peach.common.service.impl.UserServiceImpl;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 继承 {@link BaseController}。
 */
@RestController
@RequestMapping("/api/cmn/user")
@Tag(name = "User接口", description = "依据代码生成，可改")
public class UserController extends BaseController<UserVO, UserServiceImpl> {

	public UserController(UserServiceImpl service) {
		super(service);
	}
}
