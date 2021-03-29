package com.szmsd.common.plugin.interfaces;

/**
 * @author zhangyuyuan
 * @date 2021-03-29 11:44
 */
public class DefaultCommonParameter {

    private Object object;

    public DefaultCommonParameter() {
    }

    public DefaultCommonParameter(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
