package com.feifei.mybatisplus.tenant.config;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.feifei.mybatisplus.tenant.utils.UserInfoUtil;
import lombok.extern.slf4j.Slf4j;
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

import static com.feifei.mybatisplus.tenant.config.MyTenantConstants.COL_ORGANIZATION_ID;
import static com.feifei.mybatisplus.tenant.config.MyTenantConstants.COL_SYS_ID;

/**
 * @author xiaofeifei
 * @date 2020-03-01
 * @since
 */
@Slf4j
@Configuration
public class MyTenantSqlParserConfig {

    @Value("${my.mybatis-plus.tenant.filter-tables}")
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
            /**
             * 对当前用户操作判断是否取消租户过滤
             * 如果授权中心服务发生宕机时，这里通过token获取用户信息应该是不可取的，可采用特定的自定义异常对结果进行处理
             * 这里并未考虑此情况，可根据自己的业务进行自定义拓展
             * 可能为小程序端的接口调用统一取消租户过滤
             * @return
             */
            @Override
            public boolean doUserFilter() {
                // 如果获取用户信息为空则直接跳过过滤，
                // 比如说小程序的接口调用不存在token, 这里直接进行跳过
                String sysId;
                String organizationId;
                try {
                    sysId = userInfoUtil.getSysId();
                    organizationId = userInfoUtil.getOrganizationId();
                } catch (Exception e) {
                    log.info("小程序端接口调用,无需获取用户信息");
                    // 如果未获取到用户信息则直接跳过，这种情况租户过滤交由开发人员手动添加
                    return true;
                }

                boolean isTrue = Objects.nonNull(userInfoUtil)
                        && Objects.nonNull(sysId)
                        && Objects.nonNull(organizationId);
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
                val sysId = userInfoUtil.getSysId();
                val organizationId = userInfoUtil.getOrganizationId();
                boolean isTrue = Objects.nonNull(userInfoUtil)
                        && Objects.nonNull(sysId)
                        && Objects.nonNull(organizationId);
                if (isTrue && where) {
                    // 如果后续存在多租户查询的情况，这里可进行拓展 拓展方式采用sys_id in(...) and organization_id in (...)

                    EqualsTo equalsToSysId = new EqualsTo();
                    equalsToSysId.setLeftExpression(new Column(COL_SYS_ID));
                    equalsToSysId.setRightExpression(new StringValue(sysId));

                    EqualsTo equalsToOrgId = new EqualsTo();
                    equalsToOrgId.setLeftExpression(new Column(COL_ORGANIZATION_ID));
                    equalsToOrgId.setRightExpression(new StringValue(organizationId));

                    return new MultiAndExpression(Stream.of(equalsToSysId, equalsToOrgId).collect(Collectors.toList()));
                }

                return null;
            }

            /**
             * 因为采用自定义的租户校验，所以这里不做处理
             *
             * @author shixiongfei
             * @date 2020-03-01
             * @updateDate 2020-03-01
             * @updatedBy shixiongfei
             * @return com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor
             */
            @Override
            public String getTenantIdColumn() {
                return StringUtils.EMPTY;
            }

            @Override
            public boolean doTableFilter(String tableName) {
                // 这里可以过滤掉不存在sys_id organization_id的表
                if (StringUtils.isBlank(filterTables)) {
                    return false;
                }
                return Stream.of(filterTables.split(StringPool.COMMA))
                        .anyMatch(t -> t.equalsIgnoreCase(tableName));
            }
        });
        // 添加租户sql解析器
        sqlParserList.add(tenantSqlParser);
        paginationInterceptor.setSqlParserList(sqlParserList);
        // 可以过滤掉一些指定的sql语句, 这里统一采用@SqlParser(filter = true)来代替手动配置
        // paginationInterceptor.setSqlParserFilter(metaObject -> false);
        return paginationInterceptor;
    }
}