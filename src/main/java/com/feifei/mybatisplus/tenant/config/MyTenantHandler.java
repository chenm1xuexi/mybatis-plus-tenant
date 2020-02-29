package com.feifei.mybatisplus.tenant.config;

import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;

/**
 * 自定义租户处理器
 *
 * @author shixiongfei
 * @date 2020-03-01
 * @since
 */
public interface MyTenantHandler extends TenantHandler {

    /**
     * 通过用户角色来判断用户是否不需要进行租户过滤
     *
     * @author shixiongfei
     * @date 2020-03-01
     * @updateDate 2020-03-01
     * @updatedBy shixiongfei
     * @param
     * @return
     */
    boolean doUserFilter();
}