package org.peach.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 全量替换某角色下的用户绑定：{@code userIds} 为空表示清空绑定。
 */
@Data
@Schema(description = "角色绑定用户：提交后覆盖该角色下全部用户")
public class BindRoleUsersDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "用户主键列表（可为空数组表示清空）")
	private List<Long> userIds;
}
