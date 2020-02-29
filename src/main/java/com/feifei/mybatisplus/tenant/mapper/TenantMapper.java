package com.feifei.mybatisplus.tenant.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.feifei.mybatisplus.tenant.DO.UserAndTenantDO;
import com.feifei.mybatisplus.tenant.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


/**
 * @author shixiongfei
 * @date 2020-03-01
 * @since
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {

    @Select("select te.username, te.password, te.sys_id as sysId, te.organization_id as organizationId, " +
            "u.address, u.telephone from t_tenant te inner join t_user u on u.id = te.user_id ${ew.customSqlSegment}")
   UserAndTenantDO getUser(@Param(Constants.WRAPPER) Wrapper queryWrapper);
}