package com.szmsd.chargerules.export;

import com.github.pagehelper.Page;
import com.szmsd.common.core.utils.QueryPage;
import com.szmsd.delivery.vo.DelOutboundExportListVO;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-23 15:16
 */
public class GradeExportQueryPage<T> implements QueryPage<T> {

    private List<T> list;


    public GradeExportQueryPage(List<T> list) {
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
