package com.feifei.mybatisplus.tenant.config;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.feifei.mybatisplus.tenant.utils.UserInfoUtil;
import lombok.val;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.util.cnfexpression.MultiAndExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author shixiongfei
 * @date 2020-03-01
 * @since
 */
@Configuration
public class MyTenantSqlParserConfig {

    @Value("${my.mybatis-plus.table-names}")
    String filterTables;

    @Autowired
    UserInfoUtil userInfoUtil;

    /**
     * 多租户属于 SQL 解析部分，依赖 MP 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        /*
         * 【测试多租户】 SQL 解析处理拦截器<br>
         */
        List<ISqlParser> sqlParserList = new ArrayList<>(1);
        MyTenantSqlParser tenantSqlParser = new MyTenantSqlParser();
        tenantSqlParser.setTenantHandler(new MyTenantHandler() {
            @Override
            public boolean doUserFilter() {
                boolean isTrue = Objects.nonNull(userInfoUtil) && Objects.nonNull(userInfoUtil.getSysId()) && Objects.nonNull(userInfoUtil.getOrganizationId());
                return !isTrue;
            }

            /**
             * Expression就是条件语句
             *
             * https://gitee.com/baomidou/mybatis-plus/issues/IZZ3M
             *
             * tenant_id in (1,2)
             * @param  where 如果是where，可以追加，不是where的情况：比如当insert时，不能insert into user(name, tenant_id) values('test', tenant_id IN (1, 2));
             * @return
             */
            @Override
            public Expression getTenantId(boolean where) {
                boolean isTrue = Objects.nonNull(userInfoUtil) && Objects.nonNull(userInfoUtil.getSysId()) && Objects.nonNull(userInfoUtil.getOrganizationId());
                if (isTrue && where) {
                    // 如果后续存在多租户查询的情况，这里可进行拓展 拓展方式采用sys_id in(...) and organization_id in (...)
                    val sysId = userInfoUtil.getSysId();
                    val organizationId = userInfoUtil.getOrganizationId();

                    EqualsTo equalsToSysId = new EqualsTo();
                    equalsToSysId.setLeftExpression(new Column("sys_id"));
                    equalsToSysId.setRightExpression(new StringValue(sysId));

                    EqualsTo equalsToOrgId = new EqualsTo();
                    equalsToOrgId.setLeftExpression(new Column("organization_id"));
                    equalsToOrgId.setRightExpression(new StringValue(organizationId));

                    return new MultiAndExpression(Stream.of(equalsToSysId, equalsToOrgId).collect(Collectors.toList()));
                } else {
                    // 校验失败则表达式返回空，这里是否以异常的形式抛出待定
                    throw new RuntimeException("获取用户信息异常，请检查");
                }
            }

            @Override
            public String getTenantIdColumn() {
                return "sys_id-organization_id";
            }

            @Override
            public boolean doTableFilter(String tableName) {
                // 这里可以过滤掉不存在sys_id organization_id的表
                if (StringUtils.isBlank(filterTables)) {
                    return false;
                }
                return Stream.of(filterTables.split(",")).anyMatch(t -> t.equalsIgnoreCase(tableName));
            }
        });
        // 添加租户sql解析器
        sqlParserList.add(tenantSqlParser);
        paginationInterceptor.setSqlParserList(sqlParserList);
        // 可以过滤掉一些指定的sql语句
        // paginationInterceptor.setSqlParserFilter(metaObject -> false);
        return paginationInterceptor;
    }


}
