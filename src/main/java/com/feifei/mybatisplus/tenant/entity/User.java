package com.feifei.mybatisplus.tenant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * @author shixiongfei
 * @date 2020-03-01
 * @since
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
@TableName("t_user")
public class User {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    Long id;

    @TableField("address")
    String address;

    @TableField("telephone")
    String telephone;
}