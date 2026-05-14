package org.peach.common.web;

import org.peach.common.dto.ResetPwdDTO;
import org.peach.common.mvc.result.ApiResult;
import org.peach.common.mvc.web.BaseController;
import org.peach.common.service.impl.UserServiceImpl;
import org.peach.common.vo.UserVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 用户管理 REST：继承 {@link BaseController} 的分页、详情与保存（返回主键）；补充切换有效与重置口令。
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "分页、详情、保存返回 id、切换有效、重置密码、系统用户物理删除")
public class UserController extends BaseController<UserVO, UserServiceImpl> {

	public UserController(UserServiceImpl service) {
		super(service);
	}

	@Operation(summary = "切换用户有效状态", description = "当前有效则逻辑删除（无效），当前无效则逻辑恢复（有效）")
	@PostMapping("/{id}/toggle-valid")
	public ApiResult<Short> toggleValid(
			@Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable Long id) {
		return ApiResult.ok(service.toggleValid(id));
	}

	@Operation(summary = "重置系统用户登录口令")
	@PostMapping("/reset-password")
	public ApiResult<Void> resetPassword(@Valid @RequestBody ResetPwdDTO body) {
		service.resetPwd(body);
		return ApiResult.ok();
	}

	@Operation(summary = "物理删除用户（仅系统用户）", description = "从库表删除行；user_type 非 system 时拒绝")
	@DeleteMapping("/{id}/hard")
	public ApiResult<Void> hardDelete(
			@Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable Long id) {
		service.hardDelete(id);
		return ApiResult.ok();
	}
}
