package com.szmsd.common.plugin.interfaces;

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
     * @param object       object
     * @param cp           cp
     * @param cacheContext cacheContext
     * @return Object
     */
    Object handlerValue(Object object, AbstractCommonParameter cp, CacheContext cacheContext);

    /**
     * Get the order value of this object.
     *
     * @return int
     */
    default int getOrder() {
        return 1;
    }

}
