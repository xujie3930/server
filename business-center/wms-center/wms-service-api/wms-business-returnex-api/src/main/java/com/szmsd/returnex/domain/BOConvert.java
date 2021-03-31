package com.szmsd.returnex.domain;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;

/**
 * @ClassName: BOConvert
 * @Description: DTO VO  entity 类转换
 * @Author: 11
 * @Date: 2021/3/26 19:33
 */
public interface BOConvert {
    /**
     * 实体类继承转换
     *
     * @param s   被转换的类型
     * @param t   要转换的类型
     * @param <T> 要转换的类型
     * @return 转换结果
     */
    default <S, T> T convert(S s, Class<T> t) {
        return JSONUtil.toBean(JSONUtil.toJsonStr(s), t);
    }

    /**
     * 实体类继承转换
     *
     * @param t   要转换的类型
     * @param <T> 要转换的类型
     * @return 转换结果
     */
    default <T> T convertThis(Class<T> t) {
        Assert.notNull(this);
        return JSONUtil.toBean(JSONUtil.toJsonStr(this), t);
    }

}
