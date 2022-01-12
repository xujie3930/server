package com.szmsd.delivery.imported;

import com.szmsd.delivery.dto.DelOutboundBatchDetailImportDto;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zhangyuyuan
 */
public class DelOutboundBatchDetailImportValidation implements ImportValidation<DelOutboundBatchDetailImportDto> {

    private final DelOutboundOuterContext outerContext;
    private final DelOutboundBatchDetailImportContext importContext;

    public DelOutboundBatchDetailImportValidation(DelOutboundOuterContext outerContext, DelOutboundBatchDetailImportContext importContext) {
        this.outerContext = outerContext;
        this.importContext = importContext;
    }

    @Override
    public boolean before() {
        Set<Integer> detailKeySet = new HashSet<>();
        List<DelOutboundBatchDetailImportDto> dataList = this.importContext.getDataList();
        for (DelOutboundBatchDetailImportDto dto2 : dataList) {
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
    public void valid(int rowIndex, DelOutboundBatchDetailImportDto object) {
        Integer sort = object.getSort();
        if (this.importContext.isNull(sort, rowIndex, 1, null, "订单顺序不能为空")) {
            return;
        }
        this.importContext.isEmpty(object.getSku(), rowIndex, 2, null, "SKU不能为空");
        this.importContext.isNull(object.getQty(), rowIndex, 3, null, "单箱装箱数量");
    }

    @Override
    public void after() {
    }
}
