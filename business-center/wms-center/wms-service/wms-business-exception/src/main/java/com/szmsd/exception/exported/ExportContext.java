package com.szmsd.exception.exported;

/**
 * @author zhangyuyuan
 * @date 2021-04-23 15:13
 */
public interface ExportContext {

    /**
     * 获取状态名称
     *
     * @param state state
     * @return String
     */
    String getStateName(String state);

}
