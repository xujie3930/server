package com.szmsd.delivery.imported;

import com.szmsd.delivery.dto.DelOutboundPackageTransferDetailImportDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 19:39
 */
public class DelOutboundPackageTransferDetailImportValidation implements ImportValidation<DelOutboundPackageTransferDetailImportDto> {

    private final DelOutboundOuterContext outerContext;
    private final DelOutboundPackageTransferDetailImportContext importContext;

    public DelOutboundPackageTransferDetailImportValidation(DelOutboundOuterContext outerContext, DelOutboundPackageTransferDetailImportContext importContext) {
        this.outerContext = outerContext;
        this.importContext = importContext;
    }

    @Override
    public boolean before() {
        Set<Integer> detailKeySet = new HashSet<>();
        List<DelOutboundPackageTransferDetailImportDto> dataList = this.importContext.getDataList();
        for (DelOutboundPackageTransferDetailImportDto dto2 : dataList) {
            Integer sort = dto2.getSort();
            if (null == sort) {
                continue;
            }
            // 标记sort存在明细信息
            detailKeySet.add(sort);
        }
        // 判断是不是每个出库单都存在明细
        Set<Integer> outerKeySet = this.outerContext.keySet();
        // 没有主单据的集合
        Set<Integer> noDetailSet = new HashSet<>();
        for (Integer integer : outerKeySet) {
            if (!detailKeySet.contains(integer)) {
                noDetailSet.add(integer);
            }
        }
        if (!noDetailSet.isEmpty()) {
            this.importContext.addMessage(new ImportMessage(1, 1, null, "订单顺序" + noDetailSet.toString() + "没有明细"));
        }
        detailKeySet.removeAll(outerKeySet);
        if (!detailKeySet.isEmpty()) {
            this.importContext.addMessage(new ImportMessage(1, 1, null, "订单顺序" + detailKeySet.toString() + "没有单据"));
        }
        return CollectionUtils.isEmpty(this.importContext.getMessageList());
    }

    @Override
    public void valid(int rowIndex, DelOutboundPackageTransferDetailImportDto object) {
        Integer sort = object.getSort();
        if (this.importContext.isNull(sort, rowIndex, 1, null, "订单顺序不能为空")) {
            return;
        }
        this.importContext.isEmpty(object.getProductName(), rowIndex, 2, null, "英文申报品名不能为空");
        this.importContext.isEmpty(object.getProductNameChinese(), rowIndex, 3, null, "中文申报品名不能为空");
        this.importContext.isNull(object.getDeclaredValue(), rowIndex, 4, null, "申报价值不能为空");
        this.importContext.isNull(object.getQty(), rowIndex, 5, null, "出库数量不能为空");
        String productAttributeName = object.getProductAttributeName();
        if (this.importContext.isEmpty(productAttributeName, rowIndex, 6, null, "产品属性不能为空")) {
            return;
        } else {
            String productAttribute = this.importContext.productAttributeCache.get(productAttributeName);
            if (!this.importContext.isEmpty(productAttribute, rowIndex, 6, productAttributeName, "产品属性不存在")) {
                object.setProductAttribute(productAttribute);
            }
        }
        String electrifiedModeName = object.getElectrifiedModeName();
        if (StringUtils.isNotEmpty(electrifiedModeName)) {
            String electrifiedMode = this.importContext.electrifiedModeCache.get(electrifiedModeName);
            if (!this.importContext.isEmpty(electrifiedMode, rowIndex, 7, electrifiedModeName, "带电信息不存在")) {
                object.setElectrifiedMode(electrifiedMode);
            }
        }
        String batteryPackagingName = object.getBatteryPackagingName();
        if (StringUtils.isNotEmpty(batteryPackagingName)) {
            String batteryPackaging = this.importContext.batteryPackagingCache.get(batteryPackagingName);
            if (!this.importContext.isEmpty(batteryPackaging, rowIndex, 8, batteryPackagingName, "电池包装不存在")) {
                object.setBatteryPackaging(batteryPackaging);
            }
        }
    }

    @Override
    public void after() {
    }
}
