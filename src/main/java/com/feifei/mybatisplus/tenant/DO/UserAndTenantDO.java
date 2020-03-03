package com.feifei.mybatisplus.tenant.DO;

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
public class UserAndTenantDO {

    String username;

    String password;

    String address;

    String telephone;

    String sysId;

    String organizationId;
}