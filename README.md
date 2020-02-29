# mybatis-plus-tenant
本实例采用sys_id + organization_id来标识一个租户，重写了mybatis-plus中的tenant_id的租户类型


经过测试单表查询，多表查询，更新，删除来校验

具体示例请查看目录
`
com.feifei.mybatisplus.tenant.MybatisPlusTenantApplicationTests
`
进行代码测试

测试前卿首先执行resources/init_sql/init.sql中的文件

默认数据源配置的数据库名为test_user