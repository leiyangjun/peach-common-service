package org.peach.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.peach.common.mybatis.annotation.ID;
import org.peach.common.mybatis.annotation.LogicDelete;
import org.peach.common.mybatis.annotation.TableName;

/**
 * 按钮-API 绑定表实体；业务字段与 {@code org.peach.common.mvc.util.ApiMeta} 命名一致，并保留 {@code apiCode} 作为稳定绑定键。
 *
 * @author leiyangjun
 */
@Data
@TableName("cmn_button_api")
@Schema(description = "按钮-API 绑定表：字段与 ApiMeta 对齐，另含 apiCode")
public class ButtonApi implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键：雪花 64 位，对应 Java long")
    @ID
    private Long id;

    @Schema(description = "按钮 ID（cmn_menu_button.id）")
    private Long buttonId;

    @Schema(description = "API 编码：建议直接使用 /apis 返回的稳定编码")
    private String apiCode;

    @Schema(description = "HTTP 方法，对应 ApiMeta.method")
    private String method;

    @Schema(description = "接口摘要，对应 ApiMeta.summary")
    private String summary;

    @Schema(description = "接口详细说明，对应 ApiMeta.description")
    private String description;

    @Schema(description = "展示用简短说明，对应 ApiMeta.apiDesc")
    private String apiDesc;

    @Schema(description = "路径模板，对应 ApiMeta.urlPath")
    private String urlPath;

    @Schema(description = "PathPattern 表达式，对应 ApiMeta.pathPattern")
    private String pathPattern;

    @Schema(description = "所属服务名，对应 ApiMeta.serviceName")
    private String serviceName;

    @Schema(description = "接口形态 admin/app/openapi，对应 ApiMeta.apiType")
    private String apiType;

    @Schema(description = "是否有效：1=有效 0=无效（逻辑删除，SMALLINT）")
    @LogicDelete
    private Short valid;

    @Schema(description = "创建人 ID：雪花 64 位，对应 Java long")
    private Long creator;

    @Schema(description = "修改人 ID：雪花 64 位，对应 Java long")
    private Long editor;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "最后更新时间")
    private Date editTime;
}
