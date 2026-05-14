package org.peach.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 码表分页查询参数（仅用于 {@code GET /dict/page} 查询串绑定），与 {@link org.peach.common.vo.DictVO} 分离。
 */
@Data
public class DictPageQuery implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "分页关键字：字典类型、标签、存储值模糊匹配（OR）")
	private String searchValue;

	/**
	 * 状态范围筛选：null=不加 status 条件（全部）；0=仅停用；1=仅启用。
	 */
	@Schema(description = "状态筛选：null=全部，0=仅停用，1=仅启用")
	private Short listStatusFlag;
}
