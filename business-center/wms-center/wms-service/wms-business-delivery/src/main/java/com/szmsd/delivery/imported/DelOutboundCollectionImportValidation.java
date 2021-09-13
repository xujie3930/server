package com.szmsd.delivery.imported;

import com.szmsd.delivery.dto.DelOutboundCollectionImportDto;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 19:39
 */
public class DelOutboundCollectionImportValidation implements ImportValidation<DelOutboundCollectionImportDto> {

    private final DelOutboundOuterContext outerContext;
    private final DelOutboundCollectionImportContext importContext;

    public DelOutboundCollectionImportValidation(DelOutboundOuterContext outerContext, DelOutboundCollectionImportContext importContext) {
        this.outerContext = outerContext;
        this.importContext = importContext;
    }

    @Override
    public void valid(int rowIndex, DelOutboundCollectionImportDto object) {
        Integer sort = object.getSort();
        if (this.importContext.isNull(sort, rowIndex, 1, null, "订单顺序不能为空")) {
            return;
        }
        // 验证订单顺序不能重复
        if (this.outerContext.containsKey(sort)) {
            this.importContext.addMessage(new ImportMessage(rowIndex, 1, null, "订单顺序不能重复"));
            return;
        }
        String warehouseCode = object.getWarehouseCode();
        if (this.importContext.isEmpty(warehouseCode, rowIndex, 2, null, "交货仓库不能为空")) {
            return;
        }
        // 外联数据
        this.outerContext.put(sort, warehouseCode);
        String shipmentRule = object.getShipmentRule();
        if (this.importContext.isEmpty(shipmentRule, rowIndex, 3, null, "物流服务不能为空")) {
            return;
        }
        // 收件人
        String consignee = object.getConsignee();
        if (this.importContext.isEmpty(consignee, rowIndex, 4, null, "收件人不能为空")) {
            return;
        }
        // 街道1
        String street1 = object.getStreet1();
        if (this.importContext.isEmpty(street1, rowIndex, 5, null, "街道1不能为空")) {
            return;
        }
        // 邮编
        String postCode = object.getPostCode();
        if (this.importContext.isEmpty(postCode, rowIndex, 9, null, "邮编不能为空")) {
            return;
        }
        // 国家不能为空
        String country = object.getCountry();
        if (this.importContext.isEmpty(country, rowIndex, 10, null, "国家不能为空")) {
            return;
        }
        String countryCode = this.getCountryCode(country);
        this.importContext.isEmpty(countryCode, rowIndex, 10, country, "国家不存在");
    }

    public String getCountryCode(String country) {
        return this.importContext.countryCache.get(country);
    }

}