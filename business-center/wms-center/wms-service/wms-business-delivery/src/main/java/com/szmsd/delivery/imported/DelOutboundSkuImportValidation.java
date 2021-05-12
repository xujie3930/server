package com.szmsd.delivery.imported;

import com.szmsd.delivery.dto.DelOutboundDetailImportDto;

/**
 * @author zhangyuyuan
 * @date 2021-05-12 14:07
 */
public class DelOutboundSkuImportValidation implements ImportValidation<DelOutboundDetailImportDto> {

    private final DelOutboundSkuImportContext importContext;
    private final ImportValidationData importValidationData;

    public DelOutboundSkuImportValidation(DelOutboundSkuImportContext importContext, ImportValidationData importValidationData) {
        this.importContext = importContext;
        this.importValidationData = importValidationData;
    }

    @Override
    public void valid(int rowIndex, DelOutboundDetailImportDto object) {
        String sku = object.getSku();
        if (this.importContext.isEmpty(sku, rowIndex, 1, null, "SKU不能为空")) {
            return;
        }
        Long qty = object.getQty();
        if (this.importContext.isNull(qty, rowIndex, 2, null, "数量不能为空")) {
            return;
        }
        String warehouseCode = this.importContext.getWarehouseCode();
        if (!this.importValidationData.exists(warehouseCode, sku)) {
            this.importContext.addMessage(new ImportMessage(rowIndex, 1, sku, "用户没有此SKU"));
            return;
        }
        // 从外联上下文中获取到对应订单顺序的仓库编码
        if (!this.importValidationData.sub(warehouseCode, sku, Math.toIntExact(qty))) {
            this.importContext.addMessage(new ImportMessage(rowIndex, 1, sku, "SKU库存不足"));
        }
    }
}
