package com.feifei.mybatisplus.tenant.config;

import com.baomidou.mybatisplus.core.parser.AbstractJsqlParser;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.cnfexpression.MultiAndExpression;

import java.util.List;
import java.util.Objects;

import static com.feifei.mybatisplus.tenant.config.MyTenantConstants.VALID_ORGANIZATION_ID;
import static com.feifei.mybatisplus.tenant.config.MyTenantConstants.VALID_SYS_ID;

/**
 * 自定义租户sql解析器 处理行级别的数据
 * <p>
 * JSqlPaser将所有的SQL语句抽象为Statement，Statement表示对数据库的一个操作。
 *
 * @author xiaofeifei
 * @date 2020-03-01
 * @since
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class MyTenantSqlParser extends AbstractJsqlParser {

    private MyTenantHandler tenantHandler;

    /**
     * select 语句处理
     */
    @Override
    public void processSelectBody(SelectBody selectBody) {
        // 判断当前用户是否需要过滤
        if (tenantHandler.doUserFilter()) {
            return;
        }
        if (selectBody instanceof PlainSelect) {
            processPlainSelect((PlainSelect) selectBody);
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            if (withItem.getSelectBody() != null) {
                processSelectBody(withItem.getSelectBody());
            }
        } else {
            SetOperationList operationList = (SetOperationList) selectBody;
            if (operationList.getSelects() != null && operationList.getSelects().size() > 0) {
                operationList.getSelects().forEach(this::processSelectBody);
            }
        }
    }

    /**
     * insert 语句处理
     */
    @Override
    public void processInsert(Insert insert) {
        // do nothing 因为MyMataObject已经帮助我们做了sys_id 和 organization_id字段的填充
    }

    /**
     * update 语句处理
     */
    @Override
    public void processUpdate(Update update) {
        // 判断当前用户是否需要过滤
        if (tenantHandler.doUserFilter()) {
            return;
        }
        final Table table = update.getTable();
        if (tenantHandler.doTableFilter(table.getName())) {
            // 过滤退出执行
            return;
        }
        update.setWhere(this.andExpression(table, update.getWhere()));
    }

    /**
     * delete 语句处理，一般采用逻辑删除，直接硬删除很少用到
     */
    @Override
    public void processDelete(Delete delete) {
        // 判断当前用户是否需要过滤
        if (tenantHandler.doUserFilter()) {
            return;
        }
        if (tenantHandler.doTableFilter(delete.getTable().getName())) {
            // 过滤退出执行
            return;
        }
        delete.setWhere(this.andExpression(delete.getTable(), delete.getWhere()));
    }

    /**
     * delete update 语句 where 处理
     */
    protected Expression andExpression(Table table, Expression where) {
        // 添加别名
        MultiAndExpression multiAndExpression = listAliasColumn(table);
        if (Objects.nonNull(where)) {
            // 如果存在where条件且添加了sys_id 和 organization_id的条件过滤则不进行租户过滤
            if (validSysAndOrgIsExists(where)) {
                return where;
            }
            if (where instanceof OrExpression) {
                return new AndExpression(multiAndExpression, new Parenthesis(where));
            } else {
                return new AndExpression(multiAndExpression, where);
            }
        }
        return multiAndExpression;
    }

    /**
     * 处理 PlainSelect
     */
    protected void processPlainSelect(PlainSelect plainSelect) {
        processPlainSelect(plainSelect, false);
    }

    /**
     * 处理 PlainSelect
     *
     * @param plainSelect ignore
     * @param addColumn   是否添加租户列,insert into select语句中需要
     */
    protected void processPlainSelect(PlainSelect plainSelect, boolean addColumn) {
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            Table fromTable = (Table) fromItem;
            // 对指定的表进行过滤，不做租户处理
            if (tenantHandler.doTableFilter(fromTable.getName())) {
                // 过滤退出执行
                return;
            }
            // 判断当前sql是否存在where条件以及是否包含sys_id和organization_id，存在则跳过租户过滤
            Expression where = plainSelect.getWhere();

            // 添加租户条件,这里的where主要看当前是否存在where查询条件
            plainSelect.setWhere(builderExpression(where, fromTable));
            // 这一步其实没必要，因为一直为false
            if (addColumn) {
                plainSelect.getSelectItems().add(new SelectExpressionItem(new Column(tenantHandler.getTenantIdColumn())));
            }
        } else {
            // 处理子查询，这里不建议，因为这涉及到嵌套查询，实际开发中拒绝存在嵌套查询
            processFromItem(fromItem);
        }

        // 处理关联查询
        List<Join> joins = plainSelect.getJoins();
        if (CollectionUtils.isNotEmpty(joins)) {
            joins.forEach(j -> {
                processJoin(j);
                processFromItem(j.getRightItem());
            });
        }

    }

    /**
     * 处理子查询等
     */
    protected void processFromItem(FromItem fromItem) {
        if (fromItem instanceof SubJoin) {
            SubJoin subJoin = (SubJoin) fromItem;
            if (subJoin.getJoinList() != null) {
                subJoin.getJoinList().forEach(this::processJoin);
            }
            if (subJoin.getLeft() != null) {
                processFromItem(subJoin.getLeft());
            }
        } else if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) fromItem;
            if (subSelect.getSelectBody() != null) {
                processSelectBody(subSelect.getSelectBody());
            }
        } else if (fromItem instanceof ValuesList) {
            logger.debug("Perform a subquery, if you do not give us feedback");
        } else if (fromItem instanceof LateralSubSelect) {
            LateralSubSelect lateralSubSelect = (LateralSubSelect) fromItem;
            if (lateralSubSelect.getSubSelect() != null) {
                SubSelect subSelect = lateralSubSelect.getSubSelect();
                if (subSelect.getSelectBody() != null) {
                    processSelectBody(subSelect.getSelectBody());
                }
            }
        }
    }

    /**
     * 处理联接语句
     */
    protected void processJoin(Join join) {
        if (join.getRightItem() instanceof Table) {
            Table fromTable = (Table) join.getRightItem();
            if (this.tenantHandler.doTableFilter(fromTable.getName())) {
                // 过滤退出执行
                return;
            }
            join.setOnExpression(builderExpression(join.getOnExpression(), fromTable));
        }
    }

    /**
     * 处理条件:
     * 支持 getTenantHandler().getTenantId()是一个完整的表达式：tenant in (1,2)
     * 默认tenantId的表达式： LongValue(1)这种依旧支持
     * <p>
     * 这里我们采用我们自己定义的方式来处理
     * sys_id + organization_id作为整个租户的唯一标识
     *
     * @param currentExpression 现有的条件：比如你原来的sql查询条件
     */
    protected Expression builderExpression(Expression currentExpression, Table table) {
        final Expression tenantExpression = listAliasColumn(table);
        Expression appendExpression;
        appendExpression = processTableAlias4CustomizedTenantIdExpression(tenantExpression, table);

        // 如果当前查询不存在where条件则直接返回带有租户条件的条件表达式
        if (Objects.isNull(currentExpression)) {
            return appendExpression;
        }

        // 如果存在where条件且添加了sys_id 和 organization_id的条件过滤则不进行租户过滤
        if (validSysAndOrgIsExists(currentExpression)) {
            return currentExpression;
        }

        if (currentExpression instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) currentExpression;
            doExpression(binaryExpression.getLeftExpression());
            doExpression(binaryExpression.getRightExpression());
        } else if (currentExpression instanceof InExpression) {
            InExpression inExp = (InExpression) currentExpression;
            ItemsList rightItems = inExp.getRightItemsList();
            if (rightItems instanceof SubSelect) {
                processSelectBody(((SubSelect) rightItems).getSelectBody());
            }
        }
        if (currentExpression instanceof OrExpression) {
            return new AndExpression(new Parenthesis(currentExpression), appendExpression);
        } else {
            return new AndExpression(currentExpression, appendExpression);
        }
    }

    protected void doExpression(Expression expression) {
        if (expression instanceof FromItem) {
            processFromItem((FromItem) expression);
        } else if (expression instanceof InExpression) {
            InExpression inExp = (InExpression) expression;
            ItemsList rightItems = inExp.getRightItemsList();
            if (rightItems instanceof SubSelect) {
                processSelectBody(((SubSelect) rightItems).getSelectBody());
            }
        }
    }

    /**
     * 目前: 针对自定义的tenantId的条件表达式[tenant_id in (1,2,3)]，无法处理多租户的字段加上表别名
     * select a.id, b.name
     * from a
     * join b on b.aid = a.id and [b.]tenant_id in (1,2) --别名[b.]无法加上 TODO
     *
     * @param expression
     * @param table
     * @return 加上别名的多租户字段表达式
     */
    protected Expression processTableAlias4CustomizedTenantIdExpression(Expression expression, Table table) {
        //cannot add table alias for customized tenantId expression,
        // when tables including tenantId at the join table poistion
        return expression;
    }

    /**
     * 租户字段别名设置
     * <p>tableName.tenantId 或 tableAlias.tenantId</p>
     *
     * @param table 表对象
     * @return 字段
     */
    protected MultiAndExpression listAliasColumn(Table table) {
        String tableName;
        if (Objects.isNull(table.getAlias())) {
            tableName = table.getName();
        } else {
            tableName = table.getAlias().getName();
        }

        MultiAndExpression multiAndExpression = (MultiAndExpression) tenantHandler.getTenantId(true);
        List<Expression> list = multiAndExpression.getList();
        list.forEach(expression -> {
            EqualsTo equalsTo = (EqualsTo) expression;
            equalsTo.setLeftExpression(new Column(tableName + StringPool.DOT + equalsTo.getLeftExpression()));
        });

        return multiAndExpression;
    }

    /**
     * 校验当前查询中是否包含系统id或组织id的校验，存在则过滤租户查询
     *
     * @param
     * @return
     * @author shixiongfei
     * @date 2020-03-01
     * @updateDate 2020-03-01
     * @updatedBy shixiongfei
     */
    protected boolean validSysAndOrgIsExists(Expression expression) {
        if (Objects.isNull(expression)) {
            return false;
        }
        String sqlWhere = expression.toString();
        return sqlWhere.contains(VALID_SYS_ID) || sqlWhere.contains(VALID_ORGANIZATION_ID);
    }
}