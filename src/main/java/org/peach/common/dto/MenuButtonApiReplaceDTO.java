package org.peach.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import org.peach.common.mvc.util.ApiMeta;

/**
 * 全量覆盖某菜单按钮实例上的 API 绑定。
 *
 * @author leiyangjun
 */
@Data
@Schema(description = "按钮 API 绑定提交体")
public class MenuButtonApiReplaceDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "来自目标服务 /apis/type/admin 的元数据列表")
	private List<ApiMeta> apis;
}
