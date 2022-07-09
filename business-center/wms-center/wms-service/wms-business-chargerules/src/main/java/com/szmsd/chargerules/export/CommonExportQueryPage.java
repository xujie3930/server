package com.szmsd.chargerules.export;

import com.github.pagehelper.Page;
import com.szmsd.common.core.utils.QueryPage;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-23 15:16
 */
public class CommonExportQueryPage<T> implements QueryPage<T> {

    private List<T> list;


    public CommonExportQueryPage(List<T> list) {
        this.list = list;
    }

    @Override
    public Page<T> getPage() {
        Page<T> page = new Page<>(1, list.size());
        page.addAll(list);
        page.setTotal(list.size());
        return page;
    }

    @Override
    public void nextPage() {
        // 下一页
    }
}
