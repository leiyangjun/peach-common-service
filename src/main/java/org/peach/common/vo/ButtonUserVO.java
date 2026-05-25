package org.peach.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;


@Data
public class ButtonUserVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "按钮类型：add、query、update、delete、other")
	private String buttonType;

	@Schema(description = "按钮名称（展示）")
	private String buttonName;

	@Schema(description = "按钮编码（全局唯一，与前后端权限标识一致）")
	private String buttonCode;

}
