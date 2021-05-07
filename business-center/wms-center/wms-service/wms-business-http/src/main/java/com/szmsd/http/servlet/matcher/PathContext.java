package com.szmsd.http.servlet.matcher;

import java.util.Collection;

/**
 * @author zhangyuyuan
 * @date 2021-05-07 10:06
 */
public interface PathContext {

    /**
     * 增加path
     *
     * @param path 路径
     */
    void add(String path);

    /**
     * 增加path
     *
     * @param paths 路径
     */
    void add(Collection<String> paths);

    /**
     * 判断是否存在path
     *
     * @param path path
     * @return boolean
     */
    boolean has(String path);
}
