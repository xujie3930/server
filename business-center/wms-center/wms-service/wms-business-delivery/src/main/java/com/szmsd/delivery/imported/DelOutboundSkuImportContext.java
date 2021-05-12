package com.szmsd.delivery.imported;

import com.szmsd.delivery.dto.DelOutboundDetailImportDto;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-05-12 14:02
 */
public class DelOutboundSkuImportContext extends ImportContext<DelOutboundDetailImportDto> {

    private final String warehouseCode;
    private final String sellerCode;

    public DelOutboundSkuImportContext(List<DelOutboundDetailImportDto> dataList, String warehouseCode, String sellerCode) {
        super(dataList);
        this.warehouseCode = warehouseCode;
        this.sellerCode = sellerCode;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public String getSellerCode() {
        return sellerCode;
    }
}
