package com.szmsd.common.core.utils.bean;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhangyuyuan
 * @date 2020-04-29 13:38
 */
public final class QueryWrapperUtil {

    /**
     * 字段过滤
     * <p>全等，模糊匹配</p>
     *
     * @param queryWrapper queryWrapper
     * @param keyword      keyword
     * @param column       column
     * @param value        value
     */
    public static void filter(QueryWrapper<?> queryWrapper, SqlKeyword keyword, String column, String value) {
        if (StringUtils.isNotEmpty(value)) {
            value = value.trim();
            if (value.length() == 0) {
                return;
            }
            if (SqlKeyword.EQ.equals(keyword)) {
                queryWrapper.eq(column, value);
            } else if (SqlKeyword.LIKE.equals(keyword)) {
                queryWrapper.like(column, value);
            } else if (SqlKeyword.NE.equals(keyword)) {
                queryWrapper.ne(column, value);
            }
        }
    }

    /**
     * 字段过滤
     * <p>全等，模糊匹配</p>
     *
     * @param queryWrapper queryWrapper
     * @param keyword      keyword
     * @param column       column
     * @param value        value
     */
    public static void filter(QueryWrapper<?> queryWrapper, SqlKeyword keyword, String column, Long value) {
        if (null != value) {
            filter(queryWrapper, keyword, column, String.valueOf(value));
        }
    }

    /**
     * 字段过滤
     * <p>全等，模糊匹配</p>
     *
     * @param queryWrapper queryWrapper
     * @param keyword      keyword
     * @param column       column
     * @param value        value
     */
    public static void filter(QueryWrapper<?> queryWrapper, SqlKeyword keyword, String column, Double value) {
        if (null != value) {
            filter(queryWrapper, keyword, column, String.valueOf(value));
        }
    }

    /**
     * 字段过滤
     * <p>全等，模糊匹配</p>
     *
     * @param queryWrapper queryWrapper
     * @param keyword      keyword
     * @param column       column
     * @param value        value
     */
    public static void filter(QueryWrapper<?> queryWrapper, SqlKeyword keyword, String column, Integer value) {
        if (null != value) {
            filter(queryWrapper, keyword, column, String.valueOf(value));
        }
    }

    /**
     * 字段过滤
     * <p>全等，模糊匹配</p>
     *
     * @param queryWrapper queryWrapper
     * @param keyword      keyword
     * @param column       column
     * @param value        value
     */
    public static void filter(QueryWrapper<?> queryWrapper, SqlKeyword keyword, String column, BigDecimal value) {
        if (null != value) {
            filter(queryWrapper, keyword, column, String.valueOf(value));
        }
    }

    /**
     * 日期字段过滤
     *
     * @param queryWrapper queryWrapper
     * @param column       column
     * @param values       values
     */
    public static void filterDate(QueryWrapper<?> queryWrapper, String column, String[] values) {
        if (ArrayUtils.isNotEmpty(values)) {
            if (StringUtils.isNotEmpty(values[0])) {
                // 大于等于 >=
                queryWrapper.ge("DATE_FORMAT(" + column + ", '%Y-%m-%d')", values[0]);
            }
            if (StringUtils.isNotEmpty(values[1])) {
                // 小于等于 <=
                queryWrapper.le("DATE_FORMAT(" + column + ", '%Y-%m-%d')", values[1]);
            }
        }
    }

    /**
     * 日期字段过滤
     *
     * @param queryWrapper queryWrapper
     * @param column       column
     * @param value        value
     */
    public static void filterDate(QueryWrapper<?> queryWrapper, String column, Date value) {
        if (null != value) {
            queryWrapper.eq(column, value);
        }
    }


}
