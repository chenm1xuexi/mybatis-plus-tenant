package com.feifei.mybatisplus.tenant.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

/**
 * @author shixiongfei
 * @date 2020-03-01
 * @since
 */
@Configuration
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "sysId", String.class, "sysId");
        this.strictInsertFill(metaObject, "organizationId", String.class, "organizationId");
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
