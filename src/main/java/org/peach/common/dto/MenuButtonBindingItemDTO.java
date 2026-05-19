package org.peach.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import org.peach.common.mvc.api.vo.ApiMeta;

/**
 * 菜单保存时单条按钮绑定：字典主键 + 该按钮下 API 全量列表（与 {@link MenuButtonApiReplaceDTO} 语义一致）。
 *
 * @author leiyangjun
 */
@Data
@Schema(description = "菜单提交体中的单条按钮绑定")
public class MenuButtonBindingItemDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "cmn_button 字典主键 id")
	private Long dictButtonId;

	@Schema(description = "该按钮实例绑定 API 列表；空列表表示清空")
	private List<ApiMeta> apis;
}
