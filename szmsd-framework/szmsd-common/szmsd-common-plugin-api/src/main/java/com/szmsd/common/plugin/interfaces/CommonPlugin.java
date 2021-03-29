package com.szmsd.common.plugin.interfaces;

import java.util.List;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-03-29 9:47
 */
public interface CommonPlugin {

    /**
     * 类型
     *
     * @return String
     */
    String supports();

    /**
     * 处理
     *
     * @param list         list
     * @param cp           cp
     * @param cacheContext cacheContext
     * @return Object
     */
    Map<Object, Object> handlerValue(List<Object> list, DefaultCommonParameter cp, CacheContext cacheContext);

    /**
     * Get the order value of this object.
     *
     * @return int
     */
    default int getOrder() {
        return 1;
    }

}
