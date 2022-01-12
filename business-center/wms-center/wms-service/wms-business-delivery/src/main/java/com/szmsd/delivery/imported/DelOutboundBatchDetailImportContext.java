package com.szmsd.delivery.imported;

import com.szmsd.delivery.dto.DelOutboundBatchDetailImportDto;

import java.util.List;

/**
 * @author zhangyuyuan
 */
public class DelOutboundBatchDetailImportContext extends ImportContext<DelOutboundBatchDetailImportDto> {

    public DelOutboundBatchDetailImportContext(List<DelOutboundBatchDetailImportDto> dataList) {
        super(dataList);
    }

}
