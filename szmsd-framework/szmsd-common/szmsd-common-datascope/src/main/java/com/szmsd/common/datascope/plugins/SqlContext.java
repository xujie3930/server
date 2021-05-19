package com.szmsd.common.datascope.plugins;

/**
 * @author zhangyuyuan
 */
public class SqlContext {

    private static final ThreadLocal<SqlContext> contextHolder = new ThreadLocal<>();
    private String sql;

    private SqlContext() {
    }

    public static synchronized SqlContext getCurrentContext() {
        SqlContext context = contextHolder.get();
        if (context == null) {
            contextHolder.set(new SqlContext());
            context = contextHolder.get();
        }
        return context;
    }

    public void clear() {
        this.sql = null;
    }

    public String getSqlAndClear() {
        String returnSql = this.sql;
        this.sql = null;
        return returnSql;
    }

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
