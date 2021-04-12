package com.szmsd.delivery.imported;

import com.szmsd.delivery.dto.DelOutboundImportDto;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 19:39
 */
public class DelOutboundImportValidation implements ImportValidation<DelOutboundImportDto> {

    private final DelOutboundOuterContext outerContext;
    private final DelOutboundImportContext importContext;
    private final NormalImportValidation normalImportValidation;
    private final SelfPickImportValidation selfPickImportValidation;

    public DelOutboundImportValidation(DelOutboundOuterContext outerContext, DelOutboundImportContext importContext) {
        this.outerContext = outerContext;
        this.importContext = importContext;
        this.normalImportValidation = new NormalImportValidation(importContext);
        this.selfPickImportValidation = new SelfPickImportValidation(importContext);
    }

    @Override
    public void valid(int rowIndex, DelOutboundImportDto object) {
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
        if (this.importContext.isEmpty(warehouseCode, rowIndex, 2, null, "仓库代码不能为空")) {
            return;
        }
        // 外联数据
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
            this.normalImportValidation.valid(rowIndex, object);
        } else if (DelOutboundOrderTypeEnum.SELF_PICK.getCode().equals(orderType)) {
            this.selfPickImportValidation.valid(rowIndex, object);
        }
        // 备注长度限制
        String remark = object.getRemark();
        if (StringUtils.isNotEmpty(remark)) {
            if (remark.length() > 255) {
                this.importContext.addMessage(new ImportMessage(rowIndex, 15, null, "备注不能超过255个字符"));
            }
        }
    }

    public String getOrderType(String orderTypeName) {
        return this.importContext.orderTypeCache.get(orderTypeName);
    }

    static class NormalImportValidation implements ImportValidation<DelOutboundImportDto> {
        private final DelOutboundImportContext importContext;

        NormalImportValidation(DelOutboundImportContext importContext) {
            this.importContext = importContext;
        }

        @Override
        public void valid(int rowIndex, DelOutboundImportDto object) {
            // 物流服务不能为空
            this.importContext.isEmpty(object.getShipmentRule(), rowIndex, 4, null, "物流服务不能为空");
            // 收件人姓名不能为空
            this.importContext.isEmpty(object.getConsignee(), rowIndex, 5, null, "收件人姓名不能为空");
            // 街道1不能为空
            this.importContext.isEmpty(object.getStreet1(), rowIndex, 6, null, "收件人姓名不能为空");
            // 城镇/城市不能为空
            this.importContext.isEmpty(object.getCity(), rowIndex, 8, null, "收件人姓名不能为空");
            // 州/省不能为空
            this.importContext.isEmpty(object.getStateOrProvince(), rowIndex, 9, null, "收件人姓名不能为空");
            // 邮编不能为空
            this.importContext.isEmpty(object.getPostCode(), rowIndex, 10, null, "收件人姓名不能为空");
            // 国家不能为空
            String country = object.getCountry();
            if (this.importContext.isEmpty(country, rowIndex, 11, null, "国家不能为空")) {
                return;
            }
            String countryCode = this.getCountryCode(country);
            this.importContext.isEmpty(countryCode, rowIndex, 11, country, "国家不存在");
        }

        public String getCountryCode(String country) {
            return this.importContext.countryCache.get(country);
        }
    }

    static class SelfPickImportValidation implements ImportValidation<DelOutboundImportDto> {
        private final DelOutboundImportContext importContext;

        SelfPickImportValidation(DelOutboundImportContext importContext) {
            this.importContext = importContext;
        }

        @Override
        public void valid(int rowIndex, DelOutboundImportDto object) {
            // 提货方式不能为空
            String deliveryMethodName = object.getDeliveryMethodName();
            if (this.importContext.isEmpty(deliveryMethodName, rowIndex, 13, null, "提货方式不能为空")) {
                return;
            }
            String deliveryMethod = this.getDeliveryMethod(deliveryMethodName);
            this.importContext.isEmpty(deliveryMethod, rowIndex, 13, deliveryMethodName, "提货方式不存在");
            // 提货日期不能为空
            this.importContext.isNull(object.getDeliveryTime(), rowIndex, 14, null, "预计提货时间不能为空");
            // 自提人不能为空
            this.importContext.isEmpty(object.getDeliveryAgent(), rowIndex, 13, null, "自提人不能为空");
            // 提货人联系方式/快递单号不能为空
            this.importContext.isEmpty(object.getDeliveryInfo(), rowIndex, 14, null, "提货人联系方式/快递单号不能为空");
        }

        public String getDeliveryMethod(String deliveryMethodName) {
            return this.importContext.deliveryMethodCache.get(deliveryMethodName);
        }
    }
}
