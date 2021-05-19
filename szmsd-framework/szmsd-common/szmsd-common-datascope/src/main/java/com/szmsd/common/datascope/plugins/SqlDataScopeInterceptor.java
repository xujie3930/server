package com.szmsd.common.datascope.plugins;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.szmsd.common.core.exception.com.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLSyntaxErrorException;
import java.util.List;
import java.util.Properties;

/**
 * @author zhangyuyuan
 * @date 2020-12-02 002 17:52
 */
@Intercepts(
        {
                @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        }
)
public class SqlDataScopeInterceptor implements Interceptor {
    private final Logger logger = LoggerFactory.getLogger(SqlDataScopeInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Executor executor = (Executor) invocation.getTarget();

        Object[] invocationArgs = invocation.getArgs();

        MappedStatement mappedStatement = (MappedStatement) invocationArgs[0];

        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        if (!SqlCommandType.SELECT.equals(sqlCommandType)) {
            return invocation.proceed();
        }

        Object parameter = invocationArgs[1];
        RowBounds rowBounds = (RowBounds) invocationArgs[2];
        ResultHandler<?> resultHandler = (ResultHandler<?>) invocationArgs[3];

        CacheKey cacheKey;
        BoundSql boundSql;

        //由于逻辑关系，只会进入一次
        if (invocationArgs.length == 4) {
            //4 个参数时
            boundSql = mappedStatement.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
        } else {
            //6 个参数时
            cacheKey = (CacheKey) invocationArgs[4];
            boundSql = (BoundSql) invocationArgs[5];
        }
        SqlContext sqlContext = SqlContext.getCurrentContext();
        if (null != sqlContext) {
            String sqlContextSql = sqlContext.getSql();
            logger.info("====》获取到需要拦截的sql:{}", sqlContextSql);
            if (StringUtils.isNotEmpty(sqlContextSql)) {
                String originalSql = boundSql.getSql();
                // 处理sql
                String dataScopeSql = parserSql(originalSql, sqlContextSql);
                BoundSql dataScopeBoundSql = new BoundSql(mappedStatement.getConfiguration(), dataScopeSql, boundSql.getParameterMappings(), parameter);
                return executor.query(mappedStatement, parameter, rowBounds, resultHandler, cacheKey, dataScopeBoundSql);
            }
        }
        return executor.query(mappedStatement, parameter, rowBounds, resultHandler, cacheKey, boundSql);
    }

    public SQLStatement parser(String sql, String dbType) throws SQLSyntaxErrorException {
        List<SQLStatement> list = SQLUtils.parseStatements(sql, dbType);
        if (list.size() > 1) {
            throw new SQLSyntaxErrorException("MultiQueries is not supported,use single query instead ");
        }
        return list.get(0);
    }

    private String parserSql(String originalSql, String dataScopeSql) {
        try {
            String dbType = "mysql";
            SQLSelectStatement statement = (SQLSelectStatement) parser(originalSql, dbType);
            SQLSelect select = statement.getSelect();
            SQLSelectQueryBlock query = (SQLSelectQueryBlock) select.getQuery();
            query.addCondition(dataScopeSql);
            return statement.toString();
        } catch (SQLSyntaxErrorException e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("999", "业务SQL语句错误");
        } catch (ParserException e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("999", "新增条件SQL语句错误");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("999", "解析SQL语句错误");
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        this.logger.info(properties.toString());
    }
}
