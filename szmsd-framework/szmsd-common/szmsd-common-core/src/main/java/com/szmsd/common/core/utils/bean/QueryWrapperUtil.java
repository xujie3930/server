package com.szmsd.common.core.utils.bean;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.szmsd.common.core.utils.StringToolkit;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;

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
            } else if (SqlKeyword.IN.equals(keyword)) {
                //特殊符合做完字符的拆解\n|\r|\\s|，|;|,
                Set<String> newValues = new LinkedHashSet<>();
                for(String val: StringToolkit.getCodeByArray(value)){
                    val = val.trim();
                    if (val.length() > 0) {
                        newValues.add(val);
                    }
                }
                if(newValues.size() == 0){
                    return;
                }
                queryWrapper.in(column, newValues);
            }
        }
    }


    /**
     * 字段like
     *
     * @param queryWrapper queryWrapper
     * @param sqlLike      sqlLike
     * @param column       column
     * @param value        value
     */
    public static void filter(QueryWrapper<?> queryWrapper, SqlLike sqlLike, String column, String value) {
        if (StringUtils.isNotEmpty(value)) {
            value = value.trim();
            if (value.length() == 0) {
                return;
            }
            if (SqlLike.DEFAULT.equals(sqlLike)) {
                queryWrapper.like(column, value);
            } else if (SqlLike.LEFT.equals(sqlLike)) {
                queryWrapper.likeLeft(column, value);
            } else if (SqlLike.RIGHT.equals(sqlLike)) {
                queryWrapper.likeRight(column, value);
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
            if (values.length > 1 && StringUtils.isNotEmpty(values[1])) {
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
