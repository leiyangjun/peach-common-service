package org.peach.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.peach.common.mybatis.annotation.ID;
import org.peach.common.mybatis.annotation.LogicDelete;
import org.peach.common.mybatis.annotation.SearchValue;
import org.peach.common.mybatis.annotation.TableName;
import org.peach.common.mybatis.annotation.Unique;


/**
 * 见类名。
 *
 * @author leiyangjun
 */
@Data
@TableName("cmn_user")
@Schema(description = "用户：按 user_type 区分系统侧（system）与应用端（app）；含可选证件信息")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键：雪花 64 位，对应 Java long")
    @ID
    private Long id;

    @Schema(description = "用户类型：system=系统侧，app=应用端（与库约束一致）")
    private String userType;

    @Schema(description = "登录名；系统侧必填（由约束保证），app 端可空（手机/三方为主）；与 BaseMapper#checkExist 唯一键语义一致")
    @Unique
    @SearchValue
    private String username;

    @Schema(description = "密码摘要；免密或纯三方登录可为空")
    private String password;

    @Schema(description = "昵称")
    @SearchValue
    private String nickname;

    @Schema(description = "真实姓名或对内展示名")
    @SearchValue
    private String realName;

    @Schema(description = "手机号")
    @SearchValue
    private String mobile;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像地址或对象存储键")
    private String avatar;

    @Schema(description = "性别：0 未知，1 男，2 女")
    private Short gender;

    @Schema(description = "证件类型编码（与业务字典一致），可空")
    private String certType;

    @Schema(description = "证件号码，可空")
    private String certNo;

    @Schema(description = "最近登录时间")
    private Date lastLoginTime;

    @Schema(description = "最近登录终端（如 ADMIN_WEB、MINI_APP）")
    private String lastLoginClient;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否有效：1=有效 0=无效（逻辑删除标记，配合 logicDeleteByKey / logicRecoveryByKey）")
    @LogicDelete(valid = 1, invalid = 0)
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
