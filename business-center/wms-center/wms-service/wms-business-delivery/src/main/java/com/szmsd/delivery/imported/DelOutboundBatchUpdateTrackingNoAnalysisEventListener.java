package com.szmsd.delivery.imported;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.dto.DelOutboundBatchUpdateTrackingNoDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-07-06 17:07
 */
public class DelOutboundBatchUpdateTrackingNoAnalysisEventListener extends AnalysisEventListener<DelOutboundBatchUpdateTrackingNoDto> {

    private final List<DelOutboundBatchUpdateTrackingNoDto> list;

    public DelOutboundBatchUpdateTrackingNoAnalysisEventListener() {
        this.list = new ArrayList<>();
    }

    @Override
    public void invoke(DelOutboundBatchUpdateTrackingNoDto data, AnalysisContext context) {
        if (StringUtils.isNotEmpty(data.getOrderNo()) && StringUtils.isNotEmpty(data.getTrackingNo())) {
            list.add(data);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }

    public List<DelOutboundBatchUpdateTrackingNoDto> getList() {
        return list;
    }
}
