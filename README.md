# mybatis-plus-tenant

考虑到很多企业的租户隔离并非就是通过一个字段来做区分，存在最初设计的缺陷或者是业务场景导致多个字段来确定一个租户类型的情况，
随着项目或者产品不断的更新迭代，多个字段来确定租户也便成为了某些企业的租户定义标准。
mybatis-plus对多个字段来确定一个租户来实现租户数据隔离是不支持的，好在mybatis-plus提供了我们自定义拓展的方案。
***

本实例采用sys_id + organization_id来标识一个租户，重写了mybatis-plus中的tenant_id的租户类型
sysId => 系统id
organizationId => 组织id

经过测试单表查询，多表查询，更新，删除来校验租户过滤的可用性，读者可结合自己的实际业务需求来进行相应的拓展来改写，
该实例可用于生产环境，但是请提前确定应用场景以及结合自己的实际需求来完成

***

**快速启动测试指南：**
* 创建数据库 test_user
* 执行初始化sql脚本 路径 => mybatis-plus-tenant/src/main/resources/init_sql/init.sql
* 启动服务 MybatisPlusTenantApplication
* 执行响应的crud测试用例

具体示例请查看目录
`
com.feifei.mybatisplus.tenant.MybatisPlusTenantApplicationTests
`
进行代码测试

测试前请先执行resources/init_sql/init.sql中的文件完成数据库的初始化

本示例测试的默认数据源的数据库名为test_user

针对不同的业务场景可能不需要用到租户隔离，可在执行的mapper文件的方法上加上注解@SqlParser(filter = true)
（这里仅针对自己所编写的sql语句）
示例可查看
```
@SqlParser(filter = true)
   @Select("select te.username, te.password, te.sys_id as sysId, te.organization_id as organizationId, " +
            "u.address, u.telephone from t_tenant te inner join t_user u on u.id = te.user_id ${ew.customSqlSegment}")
TenantMapper.getUser()
```

以此来过滤掉租户条件的添加

因为本示例中也用到了mybatis-plus 字段自动填充插件，具体示例请查看java源文件
` 
com.feifei.mybatisplus.tenant.config.MyMetaObjectHandler
`

***

mybatis-plus是对mybatis框架的一个拓展框架，拓展了很多实际场景中便捷的插件，我们可查看官网地址[mybatis-plus](https://mp.baomidou.com/guide/)

***

***作者闲话***
<br>
不过万物皆有好坏，mybatis-plus在提供便捷开发功能的同时，也会存在一些不合理的地方，比如说mybatis的特性就是让我们开发人员将精力集中在
sql语句的编写上，而不需要考虑ORM,结果映射等操作。
采用mybatis-plus后，sql语句的可读性会变得很差，因为mybatis-plus采用sql语句的拼接方式来完成，
从开发人员的角度，开发速度得到了显著提升，但是从维护方面来看，后续新的维护人员会变得很痛苦。

因此是否选用mybatis-plus作为持久层框架要时不同的场景来抉择，如果想快速开发业务，实现敏捷开发，mybatis-plus是非常完美的选择。
如果是对公司的核心产品的更新迭代编者不建议采用mybatis-plus作为持久化的框架，采用原生的mybatis是非常不错的选择，mybatis也提供非常多的可拓展功能，比如说sql拦截等。
如果只想采用mybatis-plus，建议将项目业务领域和持久化领域做好划分(这个也需要看具体企业的开发的架构模式，基于TDD模式开发还是DDD，还是传统的集中式3层架构等)。
如果涉及到业务迭代快的场景，尽量减少多表联查，采用单表或者不大于3表联查的方式也非常有利于日后的业务需求的变更。
<br>