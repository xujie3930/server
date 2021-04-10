package com.szmsd.delivery.imported;

import com.szmsd.delivery.dto.DelOutboundDetailImportDto2;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 19:39
 */
public class DelOutboundDetailImportValidation implements ImportValidation<DelOutboundDetailImportDto2> {

    private final DelOutboundOuterContext outerContext;
    private final DelOutboundDetailImportContext importContext;
    private final ImportValidationData importValidationData;

    public DelOutboundDetailImportValidation(DelOutboundOuterContext outerContext, DelOutboundDetailImportContext importContext, ImportValidationData importValidationData) {
        this.outerContext = outerContext;
        this.importContext = importContext;
        this.importValidationData = importValidationData;
    }

    @Override
    public boolean before() {
        Set<Integer> detailKeySet = new HashSet<>();
        List<DelOutboundDetailImportDto2> dataList = this.importContext.getDataList();
        for (DelOutboundDetailImportDto2 dto2 : dataList) {
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
    public void valid(int rowIndex, DelOutboundDetailImportDto2 object) {
        Integer sort = object.getSort();
        if (this.importContext.isNull(sort, rowIndex, 1, null, "订单顺序不能为空")) {
            return;
        }
        String sku = object.getSku();
        if (this.importContext.isEmpty(sku, rowIndex, 2, null, "SKU不能为空")) {
            return;
        }
        Integer qty = object.getQty();
        if (this.importContext.isNull(qty, rowIndex, 3, null, "数量不能为空")) {
            return;
        }
        // 从外联上下文中获取到对应订单顺序的仓库编码
        String warehouseCode = this.outerContext.get(sort);
        if (!this.importValidationData.sub(warehouseCode, sku, qty)) {
            this.importContext.addMessage(new ImportMessage(rowIndex, 2, null, "SKU库存不足"));
        }
    }

    @Override
    public void after() {
    }
}
