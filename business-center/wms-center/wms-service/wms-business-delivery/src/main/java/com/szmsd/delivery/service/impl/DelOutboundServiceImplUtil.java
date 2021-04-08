package com.szmsd.delivery.service.impl;

import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.dto.DelOutboundDetailDto;
import com.szmsd.inventory.domain.dto.InventoryOperateDto;

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
}
