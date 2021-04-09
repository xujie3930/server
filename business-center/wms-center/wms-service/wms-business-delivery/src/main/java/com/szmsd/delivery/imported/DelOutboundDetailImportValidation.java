package com.szmsd.delivery.imported;

import com.szmsd.delivery.dto.DelOutboundDetailImportDto2;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 19:39
 */
public class DelOutboundDetailImportValidation implements ImportValidation<DelOutboundDetailImportDto2> {

    private final DelOutboundOuterContext outerDelOutboundContext;

    public DelOutboundDetailImportValidation(DelOutboundOuterContext outerDelOutboundContext) {
        this.outerDelOutboundContext = outerDelOutboundContext;
    }

    @Override
    public void valid(int rowIndex, DelOutboundDetailImportDto2 object) {
        Integer sort = object.getSort();
        if (null == sort) {

        }

        this.outerDelOutboundContext.get(sort);
    }
}
