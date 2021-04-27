package com.szmsd.http.service.http.resolver;

/**
 * @author zhangyuyuan
 * @date 2021-04-27 15:17
 */
public interface Actuator {

    /**
     * 执行结果
     *
     * @param actuatorParameter actuatorParameter
     * @return boolean
     */
    boolean execute(ActuatorParameter actuatorParameter);
}
