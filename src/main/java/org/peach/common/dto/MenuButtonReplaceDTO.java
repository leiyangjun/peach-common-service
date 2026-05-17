package org.peach.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 全量覆盖某菜单下的按钮实例：仅允许传入 cmn_button 主键列表。
 *
 * @author leiyangjun
 */
@Data
@Schema(description = "菜单绑定按钮字典主键列表")
public class MenuButtonReplaceDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "cmn_button.id 列表，顺序即同菜单下排序参考（再按字典 sort_no 稳定排序）")
	private List<Long> dictButtonIds;
}
