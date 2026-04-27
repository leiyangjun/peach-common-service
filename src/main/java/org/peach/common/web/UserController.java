package org.peach.common.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.peach.common.mvc.result.ApiResult;
import org.peach.common.mvc.web.BaseController;
import org.peach.common.security.LoginUserVO;
import org.peach.common.service.impl.UserServiceImpl;
import org.peach.common.vo.UserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 继承 {@link BaseController}。 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户", description = "用户 CRUD")
public class UserController extends BaseController<UserVO, UserServiceImpl> {

	public UserController(UserServiceImpl service) {
		super(service);
	}

	@GetMapping("/current")
	@Operation(summary = "当前登录用户", description = "身份来自网关追加的查询参数 peach_user_id / peach_username / peach_subject_type（经网关校验令牌后注入）。")
	public ApiResult<LoginUserVO> current() {
		return ApiResult.ok(service.currentProfile());
	}
}
