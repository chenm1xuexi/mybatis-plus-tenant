package com.feifei.mybatisplus.tenant.utils;

import org.springframework.context.annotation.Configuration;

/**
 * @author shixiongfei
 * @date 2020-03-01
 * @since
 */
@Configuration
public class UserInfoUtil {

    public String getSysId() {
        return "sysId";
    }

    public String getOrganizationId() {
        return "organizationId";
    }
}