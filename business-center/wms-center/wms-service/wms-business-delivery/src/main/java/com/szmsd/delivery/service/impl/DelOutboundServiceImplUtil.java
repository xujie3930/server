package com.szmsd.delivery.service.impl;

import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.dto.DelOutboundDetailDto;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.inventory.domain.dto.InventoryOperateDto;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-04-08 16:56
 */
public final class DelOutboundServiceImplUtil {

    public static void handlerInventoryOperate(DelOutboundDetail detail, Map<String, InventoryOperateDto> inventoryOperateDtoMap) {
        String invoiceLineNo = String.valueOf(detail.getLineNo());
        String sku = detail.getSku();
        int qty = Math.toIntExact(detail.getQty());
        // 判断有没有包材，包材也需要冻结
        String bindCode = detail.getBindCode();
        handlerInventoryOperate(invoiceLineNo, sku, bindCode, qty, inventoryOperateDtoMap);
    }

    public static void handlerInventoryOperate(DelOutboundDetailDto detail, Map<String, InventoryOperateDto> inventoryOperateDtoMap) {
        String invoiceLineNo = String.valueOf(detail.getLineNo());
        String sku = detail.getSku();
        int qty = Math.toIntExact(detail.getQty());
        // 判断有没有包材，包材也需要冻结
        String bindCode = detail.getBindCode();
        handlerInventoryOperate(invoiceLineNo, sku, bindCode, qty, inventoryOperateDtoMap);
    }

    private static void handlerInventoryOperate(String invoiceLineNo, String sku, String bindCode, int qty, Map<String, InventoryOperateDto> inventoryOperateDtoMap) {
        handlerInventoryOperate(invoiceLineNo, sku, qty, inventoryOperateDtoMap);
        if (StringUtils.isNotEmpty(bindCode)) {
            handlerInventoryOperate(invoiceLineNo, bindCode, qty, inventoryOperateDtoMap);
        }
    }

    private static void handlerInventoryOperate(String invoiceLineNo, String sku, int qty, Map<String, InventoryOperateDto> inventoryOperateDtoMap) {
        if (inventoryOperateDtoMap.containsKey(sku)) {
            inventoryOperateDtoMap.get(sku).addQty(qty);
        } else {
            inventoryOperateDtoMap.put(sku, new InventoryOperateDto(invoiceLineNo, sku, qty));
        }
    }

    public static String getLabelBizPath(DelOutbound delOutbound) {
        // 单据类型/年/月/日/单据号
        Date createTime = delOutbound.getCreateTime();
        String datePath = DateFormatUtils.format(createTime, "yyyy/MM/dd");
        return delOutbound.getOrderType() + "/" + datePath;
    }

    /**
     * 不需要操作库存
     *
     * @param orderType 出库单类型
     * @return true不需要，false需要
     */
    public static boolean noOperationInventory(String orderType) {
        // 转运出库
        // 集运出库
        // 不需要冻结库存
        return DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(orderType)
                || DelOutboundOrderTypeEnum.COLLECTION.getCode().equals(orderType);
    }
}
