package com.szmsd.delivery.imported;

import com.szmsd.delivery.dto.DelOutboundDetailImportDto2;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 20:07
 */
public class DelOutboundDetailImportContext extends ImportContext<DelOutboundDetailImportDto2> {

    public DelOutboundDetailImportContext(List<DelOutboundDetailImportDto2> dataList) {
        super(dataList);
    }
}
