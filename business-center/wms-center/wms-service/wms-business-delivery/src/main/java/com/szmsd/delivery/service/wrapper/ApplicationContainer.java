package com.szmsd.delivery.service.wrapper;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-04-01 16:49
 */
public class ApplicationContainer {
    private final Logger logger = LoggerFactory.getLogger(ApplicationContainer.class);

    // handle map
    private final Map<String, ApplicationHandle> handleMap;
    // context
    private final ApplicationContext context;
    // end state
    private ApplicationState endState;
    // current state
    private ApplicationState currentState;

    public ApplicationContainer(ApplicationContext context, ApplicationState currentState, ApplicationState endState, ApplicationRegister register) {
        this.context = context;
        this.currentState = currentState;
        this.endState = endState;
        this.handleMap = register.register();
    }

    public void setEndState(ApplicationState endState) {
        this.endState = endState;
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
        TimeInterval timer = DateUtil.timer();
        logger.info("(1){}执行动作开始，currentState:{}, endState:{}", currentState.getClass().getSimpleName(), currentState.name(), endState.name());
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
                    logger.error(e.getMessage(), e);
                    // 处理错误异常
                    handle.errorHandler(context, e, this.currentState);
                    // 往上抛出异常
                    throw e;
                }
            }
            this.currentState = handle.nextState();
        }
        logger.info("(2){}执行动作结束，currentState:{}, endState:{}, timer:{}", currentState.getClass().getSimpleName(), currentState.name(), endState.name(), timer.intervalRestart());
    }

    /**
     * do rollback
     */
    public void rollback() {
        if (null == this.currentState) {
            throw new RuntimeException("currentState cannot be null");
        }
        if (null == this.endState) {
            throw new RuntimeException("endState cannot be null");
        }
        // 当前节点为失败节点，获取到上一个节点进行回滚
        ApplicationHandle applicationHandle = this.handleMap.get(this.currentState.name());
        if (null != applicationHandle) {
            this.currentState = applicationHandle.preState();
        }
        // end state != next state
        while (!this.endState.equals(this.currentState)) {
            ApplicationHandle handle = this.handleMap.get(this.currentState.name());
            if (null == handle) {
                throw new RuntimeException("[" + this.currentState.name() + "] handle is null");
            }
            if (handle.condition(context, this.currentState)) {
                try {
                    handle.rollback(context);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    // 处理错误异常
                    handle.errorHandler(context, e, this.currentState);
                    // 往上抛出异常
                    throw e;
                }
            }
            this.currentState = handle.preState();
        }
    }
}
