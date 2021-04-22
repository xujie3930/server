package com.szmsd.common.core.utils;

import com.github.pagehelper.Page;

/**
 * @author zhangyuyuan
 * @date 2021-04-22 19:18
 */
public interface QueryPage<T> {

    Page<T> getPage();

    void nextPage();
}
