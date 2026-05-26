package org.peach.common.vo;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 运维目录局部更新：仅白名单字段；不触碰 menu_button。
 *
 * @author leiyangjun
 * @date 2026-05-26
 */
@Data
@Schema(description = "运维目录局部更新")
public class MenuOpsPatchVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "菜单名称")
	@NotBlank
	private String menuName;

	@Schema(description = "同级排序号")
	@NotNull
	private Integer orderNo;

	@Schema(description = "图标组件名")
	@NotBlank
	private String icon;

	@Schema(description = "是否有效：1=有效 0=无效")
	@NotNull
	private Short valid;

	@Schema(description = "备注")
	private String remark;
}
