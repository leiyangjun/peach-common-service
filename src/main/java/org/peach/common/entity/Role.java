package org.peach.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.peach.common.mybatis.annotation.ID;
import org.peach.common.mybatis.annotation.LogicDelete;
import org.peach.common.mybatis.annotation.TableName;

@Data
@TableName("cmn_role")
@Schema(description = "角色表：角色编码 role_code 为稳定业务编码")
public class Role implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键：雪花 64 位，对应 Java long")
    @ID
    private Long id;

    @Schema(description = "角色编码：如 ROLE_ADMIN、ROLE_OPERATOR")
    private String roleCode;

    @Schema(description = "角色名称：用于页面展示")
    private String roleName;

    @Schema(description = "备注")
    private String remark;

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
