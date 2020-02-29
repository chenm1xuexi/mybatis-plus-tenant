package com.feifei.mybatisplus.tenant;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.feifei.mybatisplus.tenant.entity.Tenant;
import com.feifei.mybatisplus.tenant.mapper.TenantMapper;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MybatisPlusTenantApplicationTests {

    @Autowired
    TenantMapper tenantMapper;

    /**
     * 单表查询
     */
    @Test
    public void getById() {
        val tenant = tenantMapper.selectById(1);
        System.out.println("tenant = " + tenant);
    }

    /**
     * 多表查询
     * @throws Exception
     */
    @Test
    public void getAll() throws Exception {
        QueryWrapper<Tenant> wrapper = new QueryWrapper<>();
        wrapper.eq("te.id", 1);
        val userAndTenantDO = tenantMapper.getUser(wrapper);
        System.out.println("userAndTenantDO = " + userAndTenantDO);
    }

    /**
     * 保存
     */
    @Test
    public void save() {
        Tenant tenant = new Tenant().setUsername("哈哈哈").setPassword("123456").setUserId(2L);
        tenantMapper.insert(tenant);
        System.out.println("tenant = " + tenant);
    }

    /**
     * 更新
     */
    @Test
    public void update() {
        Tenant tenant = new Tenant().setId(1233839987334631425L).setUsername("哈哈利普").setPassword("321475").setUserId(3L);
        tenantMapper.updateById(tenant);
        System.out.println("tenant = " + tenant);
    }

    /**
     * 删除
     */
    @Test
    public void delete() {
        tenantMapper.deleteById(1233839987334631425L);
    }

}