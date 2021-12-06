package com.szmsd.chargerules.config;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: AnalysisListenerAbstract
 * @Description:
 * @Author: 11
 * @Date: 2021-12-05 14:35
 */
public class AnalysisListenerAbstract<T> extends AnalysisEventListener<T> {
    private List<T> resultList = new ArrayList<>();

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        resultList.add(t);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public List<T> getResultList() {
        return resultList;
    }
}
