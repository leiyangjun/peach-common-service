package org.peach.common.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MenuButtonInfoVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "菜单绑定按钮信息")
	private MenuButtonVO menuButton;

	@Schema(description = "按钮绑定API信息")
	private List<ButtonApiVO> buttonApis;
}
