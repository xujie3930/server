package com.szmsd.delivery.service.wrapper;

import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-04-01 17:18
 */
public interface ApplicationRegister {

    /**
     * register
     *
     * @return Map<String, ApplicationHandle>
     */
    Map<String, ApplicationHandle> register();
}
