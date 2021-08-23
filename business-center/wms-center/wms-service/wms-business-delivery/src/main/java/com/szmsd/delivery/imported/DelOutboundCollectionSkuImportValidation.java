package com.szmsd.delivery.imported;

import com.szmsd.delivery.dto.DelOutboundCollectionDetailImportDto;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhangyuyuan
 * @date 2021-05-12 14:07
 */
public class DelOutboundCollectionSkuImportValidation implements ImportValidation<DelOutboundCollectionDetailImportDto> {

    private final DelOutboundCollectionSkuImportContext importContext;

    public DelOutboundCollectionSkuImportValidation(DelOutboundCollectionSkuImportContext importContext) {
        this.importContext = importContext;
    }

    @Override
    public void valid(int rowIndex, DelOutboundCollectionDetailImportDto object) {
        this.importContext.isEmpty(object.getProductName(), rowIndex, 2, null, "英文申报品名不能为空");
        this.importContext.isEmpty(object.getProductNameChinese(), rowIndex, 3, null, "中文申报品名不能为空");
        this.importContext.isNull(object.getDeclaredValue(), rowIndex, 4, null, "申报价值不能为空");
        String productAttributeName = object.getProductAttributeName();
        if (this.importContext.isEmpty(productAttributeName, rowIndex, 5, null, "产品属性不能为空")) {
            return;
        } else {
            String productAttribute = this.importContext.getProductAttributeCache().get(productAttributeName);
            if (!this.importContext.isEmpty(productAttribute, rowIndex, 5, productAttributeName, "产品属性不存在")) {
                object.setProductAttribute(productAttribute);
            }
        }
        String electrifiedModeName = object.getElectrifiedModeName();
        if (StringUtils.isNotEmpty(electrifiedModeName)) {
            String electrifiedMode = this.importContext.getElectrifiedModeCache().get(electrifiedModeName);
            if (!this.importContext.isEmpty(electrifiedMode, rowIndex, 6, electrifiedModeName, "带电信息不存在")) {
                object.setElectrifiedMode(electrifiedMode);
            }
        }
        String batteryPackagingName = object.getBatteryPackagingName();
        if (StringUtils.isNotEmpty(batteryPackagingName)) {
            String batteryPackaging = this.importContext.getBatteryPackagingCache().get(batteryPackagingName);
            if (!this.importContext.isEmpty(batteryPackaging, rowIndex, 7, batteryPackagingName, "电池包装不存在")) {
                object.setBatteryPackaging(batteryPackaging);
            }
        }
        this.importContext.isEmpty(object.getProductDescription(), rowIndex, 9, null, "产品说明不能为空");
    }
}
