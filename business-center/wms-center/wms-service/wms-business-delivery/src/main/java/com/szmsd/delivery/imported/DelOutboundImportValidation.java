package com.szmsd.delivery.imported;

import com.szmsd.delivery.dto.DelOutboundImportDto;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 19:39
 */
public class DelOutboundImportValidation implements ImportValidation<DelOutboundImportDto> {

    private final DelOutboundOuterContext outerContext;
    private final DelOutboundImportContext importContext;
    private final NormalImportValidation normalImportValidation;

    public DelOutboundImportValidation(DelOutboundOuterContext outerContext, DelOutboundImportContext importContext) {
        this.outerContext = outerContext;
        this.importContext = importContext;
        this.normalImportValidation = new NormalImportValidation();
    }

    @Override
    public void valid(int rowIndex, DelOutboundImportDto object) {
        Integer sort = object.getSort();
        if (this.importContext.isNull(sort, rowIndex, 1, null, "订单顺序不能为空")) {
            return;
        }
        String warehouseCode = object.getWarehouseCode();
        if (this.importContext.isEmpty(warehouseCode, rowIndex, 2, null, "仓库代码不能为空")) {
            return;
        }
        this.outerContext.put(sort, warehouseCode);
        String orderTypeName = object.getOrderTypeName();
        if (this.importContext.isEmpty(orderTypeName, rowIndex, 3, null, "出库方式不能为空")) {
            return;
        }
        String orderType = this.getOrderType(orderTypeName);
        if (this.importContext.isEmpty(orderType, rowIndex, 3, orderTypeName, "出库方式不存在")) {
            return;
        }
        if (DelOutboundOrderTypeEnum.NORMAL.getCode().equals(orderType)) {

        } else if (DelOutboundOrderTypeEnum.DESTROY.getCode().equals(orderType)) {

        } else if (DelOutboundOrderTypeEnum.SELF_PICK.getCode().equals(orderType)) {

        }
    }

    public String getOrderType(String orderTypeName) {
        return this.importContext.orderTypeCache.get(orderTypeName);
    }

    public String getCountryCode(String country) {
        return this.importContext.countryCache.get(country);
    }

    static class NormalImportValidation implements ImportValidation<DelOutboundImportDto> {

        @Override
        public void valid(int rowIndex, DelOutboundImportDto object) {

        }
    }
}
