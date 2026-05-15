package org.peach.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 全量覆盖角色与菜单按钮实例的绑定。
 */
@Data
@Schema(description = "角色绑定菜单按钮实例主键列表")
public class RoleMenuButtonReplaceDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "cmn_menu_button.id 列表")
	private List<Long> menuButtonIds;
}
