package com.szmsd.delivery.service.wrapper;

/**
 * @author zhangyuyuan
 * @date 2021-04-01 16:09
 */
public interface ApplicationHandle {

    /**
     * 现态
     *
     * @return ApplicationState
     */
    ApplicationState quoState();

    /**
     * 处理
     *
     * @param context context
     */
    void handle(ApplicationContext context);

    /**
     * 条件
     *
     * @param context context
     * @return boolean
     */
    default boolean condition(ApplicationContext context) {
        return true;
    }

    ;

    /**
     * 次态
     *
     * @return ApplicationState
     */
    ApplicationState nextState();

    /**
     * 快照
     */
    default void snapshot() {
    }
}
