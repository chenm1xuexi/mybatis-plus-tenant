package com.feifei.mybatisplus.tenant.utils;

import org.springframework.context.annotation.Configuration;

/**
 * @author xiaofeifei
 * @date 2020-03-01
 * @since
 */
@Configuration
public class UserInfoUtil {

    /**
     * 这里只是做一个模拟，是你场景中是需要调用基础数据平台来获取用户相关信息
     *
     * @author shixiongfei
     * @date 2020-03-03
     * @updateDate 2020-03-03
     * @updatedBy shixiongfei
     * @param
     * @return
     */
    public String getSysId() {
        return "sysId";
    }

    /**
     * 这里只是做一个模拟，是你场景中是需要调用基础数据平台来获取用户相关信息
     *
     * @author shixiongfei
     * @date 2020-03-03
     * @updateDate 2020-03-03
     * @updatedBy shixiongfei
     * @param
     * @return
     */
    public String getOrganizationId() {
        return "organizationId";
    }
}