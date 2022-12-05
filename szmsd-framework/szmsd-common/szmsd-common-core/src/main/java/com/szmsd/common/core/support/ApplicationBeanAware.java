package com.szmsd.common.core.support;

public interface ApplicationBeanAware {
    /**
     * 获取bean
     *
     * @param beanClass
     * @param <T>
     * @return
     */
    default <T> T $(Class<T> beanClass) {
        return Context.getBean(beanClass);
    }
}
