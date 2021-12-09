package com.szmsd.exception.exported;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

public class DefaultSyncReadListener<T> extends AnalysisEventListener<T> {

    private final List<T> list;

    public DefaultSyncReadListener() {
        list = new ArrayList<>();
    }

    @Override
    public void invoke(T object, AnalysisContext context) {
        list.add(object);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }

    public List<T> getList() {
        return list;
    }
}
