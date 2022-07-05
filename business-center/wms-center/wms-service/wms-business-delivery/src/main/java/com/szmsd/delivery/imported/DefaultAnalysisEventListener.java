package com.szmsd.delivery.imported;

import com.alibaba.excel.context.AnalysisContext;

public class DefaultAnalysisEventListener<T> extends AbstractAnalysisEventListener<T> {

    private DefaultAnalysisFormat<T> defaultAnalysisFormat;

    public DefaultAnalysisEventListener() {
    }

    public DefaultAnalysisEventListener(DefaultAnalysisFormat<T> defaultAnalysisFormat) {
        this.defaultAnalysisFormat = defaultAnalysisFormat;
    }

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        if (null != this.defaultAnalysisFormat) {
            super.invoke(this.defaultAnalysisFormat.format(t), analysisContext);
        } else {
            super.invoke(t, analysisContext);
        }
    }
}
