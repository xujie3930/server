package com.szmsd.delivery.imported;

import com.szmsd.delivery.dto.DelOutboundBatchImportDto;

/**
 * @author zhangyuyuan
 */
public class DelOutboundBatchImportValidation implements ImportValidation<DelOutboundBatchImportDto> {

    private final DelOutboundOuterContext outerContext;
    private final DelOutboundBatchImportContext importContext;

    public DelOutboundBatchImportValidation(DelOutboundOuterContext outerContext, DelOutboundBatchImportContext importContext) {
        this.outerContext = outerContext;
        this.importContext = importContext;
    }

    @Override
    public void valid(int rowIndex, DelOutboundBatchImportDto object) {
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
        if (this.importContext.isEmpty(warehouseCode, rowIndex, 3, null, "交货仓库不能为空")) {
            return;
        }
        // 外联数据
        this.outerContext.put(sort, warehouseCode);
        // 收件人
        String consignee = object.getConsignee();
        this.importContext.isEmpty(consignee, rowIndex, 9, null, "收件人姓名不能为空");
        // 街道1
        String street1 = object.getStreet1();
        if (!this.importContext.isEmpty(street1, rowIndex, 10, null, "街道1不能为空")) {
            this.importContext.stringLength(street1, 500, rowIndex, 10, "街道1不能超过50个字符");
        }
        this.importContext.stringLength(object.getStreet2(), 500, rowIndex, 11, "街道2不能超过500个字符");
        this.importContext.stringLength(object.getCity(), 50, rowIndex, 12, "城镇/城市不能超过50个字符");
        this.importContext.stringLength(object.getStateOrProvince(), 50, rowIndex, 13, "州/省不能超过50个字符");
        // 邮编
        String postCode = object.getPostCode();
        if (!this.importContext.isEmpty(postCode, rowIndex, 14, null, "邮编不能为空")) {
            this.importContext.stringLength(postCode, 50, rowIndex, 14, "邮编不能超过50个字符");
        }
        // 国家不能为空
        String country = object.getCountry();
        if (this.importContext.isEmpty(country, rowIndex, 15, null, "国家不能为空")) {
            return;
        }
        String countryCode = this.getCountryCode(country);
        this.importContext.isEmpty(countryCode, rowIndex, 15, country, "国家不存在");
        this.importContext.isEmpty(object.getPhoneNo(), 16, rowIndex, null, "联系方式不能为空");
    }

    public String getCountryCode(String country) {
        return this.importContext.countryCache.get(country);
    }

}
