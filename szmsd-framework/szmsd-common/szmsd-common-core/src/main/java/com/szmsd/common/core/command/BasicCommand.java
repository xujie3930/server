package com.szmsd.common.core.command;

import com.szmsd.common.core.support.ApplicationBeanAware;
import com.szmsd.common.core.support.ColorOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * 所有command命令集成该基类
 *
 * @param <R> 返回的结果类型
 */
public abstract class BasicCommand<R> implements Command<R>, ApplicationBeanAware {

    private boolean executed = false;

    private boolean rollbacked = false;

    private String errorMsg;

    private Long executeTimeMillis;

    protected final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    public BasicCommand() {
        autowiredBean();
    }

    /**
     * 前置校验器，需要自行throw exception
     *
     * @return
     */
    protected void beforeDoExecute() {
        /** no op */
    }

    protected void autowiredBean() {
        Field[] declaredFields = this.getClass().getDeclaredFields();
        Arrays.asList(declaredFields).forEach(field -> {
            field.setAccessible(true);
            Annotation[] annotations = field.getAnnotations();
            boolean anyMatch = Arrays.asList(annotations).stream().map(Annotation::annotationType)
                    .anyMatch(aClass -> aClass.isAssignableFrom(Autowired.class));
            if (anyMatch) {
                ReflectionUtils.setField(field, this, $(field.getType()));
            }
        });
    }

    /**
     * 执行
     *
     * @return
     */
    @Override
    public final R execute() {
        if (executed) {
            throw new RuntimeException("command: " + this.getClass().getName() + " 已经执行过,不允许重复执行!");
        }

        long startTime = System.currentTimeMillis();
        R r;
        try {
            beforeDoExecute();
            r = doExecute();
            executed = true;
            afterExecuted(r);
        } catch (Exception ex) {
            rollbacked = true;
            errorMsg = ex.getMessage();
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            executeTimeMillis = System.currentTimeMillis() - startTime;
            if (executeTimeMillis > getLimitWarnningTimeMillis()) {
                logger.warn(ColorOutput.BRIGHT_RED("警告：{} 耗时: {} (ms)"), this.getClass().getSimpleName(), executeTimeMillis);
            } else {
                logger.debug("{} 耗时: {} (ms)", this.getClass().getSimpleName(), executeTimeMillis);
            }

            if(rollbacked){
                rollback(errorMsg);
            }
        }
        return r;
    }

    /**
     * 执行器
     *
     * @return 返回结果
     */
    protected abstract R doExecute() throws Exception;

    /**
     * 后置处理器，比如记录日志啥的
     */
    protected void afterExecuted(R result) throws Exception {
        /** no op */
    }

    /**
     * 异常执行
     * @throws Exception
     */
    protected void rollback(String errorMsg) {

    }

    /**
     * 是否执行过
     *
     * @return
     */
    @Override
    public boolean isExecuted() {
        return executed;
    }

    /**
     * 获取执行的耗时（毫秒）
     *
     * @return
     */
    @Override
    public Long getTimeMillis() {
        return executeTimeMillis;
    }

    /**
     * 超过该阈值限制的时间会告警
     *
     * @return
     */
    protected long getLimitWarnningTimeMillis() {
        return 500L;
    }

    public Logger getLogger() {
        return logger;
    }

}
