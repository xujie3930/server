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
     * 回滚
     *
     * @param context context
     */
    void rollback(ApplicationContext context);

    /**
     * 条件
     *
     * @param context      context
     * @param currentState currentState
     * @return boolean
     */
    boolean condition(ApplicationContext context, ApplicationState currentState);

    /**
     * 次态
     *
     * @return ApplicationState
     */
    ApplicationState nextState();

    /**
     * 错误处理
     *
     * @param context      context
     * @param throwable    throwable
     * @param currentState currentState
     */
    void errorHandler(ApplicationContext context, Throwable throwable, ApplicationState currentState);

    /**
     * 对错误处理进行处理
     */
    abstract class AbstractApplicationHandle implements ApplicationHandle {

        @Override
        public void rollback(ApplicationContext context) {

        }

        @Override
        public void errorHandler(ApplicationContext context, Throwable throwable, ApplicationState currentState) {

        }
    }
}
