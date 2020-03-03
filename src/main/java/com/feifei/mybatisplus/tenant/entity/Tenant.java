package com.feifei.mybatisplus.tenant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * @author xiaofeifei
 * @date 2020-03-01
 * @since
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
@TableName("t_tenant")
public class Tenant {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    Long id;

    @TableField("username")
    String username;

    @TableField("password")
    String password;

    @TableField(value = "sys_id", fill = FieldFill.INSERT)
    String sysId;

    @TableField(value = "organization_id", fill = FieldFill.INSERT)
    String organizationId;

    @TableField(value = "user_id", fill = FieldFill.INSERT)
    Long userId;

}