package com.szmsd.delivery.service.wrapper;

import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-04-01 16:49
 */
public class ApplicationContainer {

    // handle map
    private final Map<String, ApplicationHandle> handleMap;
    // context
    private final ApplicationContext context;
    // end state
    private final ApplicationState endState;
    // current state
    private ApplicationState currentState;

    public ApplicationContainer(ApplicationContext context, ApplicationState currentState, ApplicationState endState, ApplicationRegister register) {
        this.context = context;
        this.currentState = currentState;
        this.endState = endState;
        this.handleMap = register.register();
    }

    /**
     * do action
     */
    public void action() {
        if (null == this.currentState) {
            throw new RuntimeException("currentState cannot be null");
        }
        if (null == this.endState) {
            throw new RuntimeException("endState cannot be null");
        }
        // end state != next state
        while (!this.endState.equals(this.currentState)) {
            ApplicationHandle handle = this.handleMap.get(this.currentState.name());
            if (null == handle) {
                throw new RuntimeException("[" + this.currentState.name() + "] handle is null");
            }
            if (handle.condition(context, this.currentState)) {
                try {
                    handle.handle(context);
                } catch (Exception e) {
                    // 处理错误异常
                    handle.errorHandler(context, e, this.currentState);
                    // 往上抛出异常
                    throw e;
                }
            }
            this.currentState = handle.nextState();
        }
    }
}
