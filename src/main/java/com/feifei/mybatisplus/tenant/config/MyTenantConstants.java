package com.feifei.mybatisplus.tenant.config;

/**
 * 自定义租户常量类
 *
 * @author shixiongfei
 * @date 2020-03-01
 * @since
 */
public interface MyTenantConstants {

    /**
     * 系统id
     */
    String COL_SYS_ID = "sys_id";

    /**
     * 组织id
     */
    String COL_ORGANIZATION_ID = "organization_id";

    /**
     * 校验系统id
     */
    String VALID_SYS_ID = "sys_id = ?";

    /**
     * 校验组织id
     */
    String VALID_ORGANIZATION_ID = "organization_id = ?";
}