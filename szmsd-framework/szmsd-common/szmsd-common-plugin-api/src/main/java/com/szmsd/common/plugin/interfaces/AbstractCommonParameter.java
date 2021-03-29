package com.szmsd.common.plugin.interfaces;

/**
 * @author zhangyuyuan
 * @date 2021-03-29 11:44
 */
public abstract class AbstractCommonParameter {

    private Object object;

    public AbstractCommonParameter() {
    }

    public AbstractCommonParameter(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
