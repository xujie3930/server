package com.szmsd.http.servlet.header;

/**
 * @author zhangyuyuan
 * @date 2021-04-30 14:38
 */
public interface RequestForwardHeader {

    /**
     * name
     *
     * @return String
     */
    String getName();

    /**
     * value
     *
     * @return String
     */
    String getValue();
}
