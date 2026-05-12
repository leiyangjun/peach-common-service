package org.peach.common.dto;

import java.io.Serial;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 系统用户重置登录口令入参。
 */
@Data
@Schema(description = "重置密码")
public class ResetPwdDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@NotNull
	@Schema(description = "用户主键")
	private Long id;

	@NotBlank
	@Size(min = 6, max = 64)
	@Schema(description = "新口令（明文，服务端 BCrypt 后落库）")
	private String newPassword;
}
