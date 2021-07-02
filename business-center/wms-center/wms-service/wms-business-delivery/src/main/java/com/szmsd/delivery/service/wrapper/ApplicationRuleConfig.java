package com.szmsd.delivery.service.wrapper;

import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author zhangyuyuan
 * @date 2021-04-02 16:21
 */
public final class ApplicationRuleConfig {

    /**
     * 提审规则
     */
    private static final Map<String, Set<String>> bringVerifyRuleMap = new HashMap<>();
    /**
     * 出库规则
     */
    private static final Map<String, Set<String>> shipmentRuleMap = new HashMap<>();


    static {
        // =======================================================================================
        // 提审配置
        // =======================================================================================
        // 没配置默认执行所有步骤
        // 提审配置 - 【普通出库】
        // 执行所有步骤
        // 提审配置 - 【销毁出库】
        Set<String> bringVerifyDestroySet = new HashSet<>();
        bringVerifyDestroySet.add(BringVerifyEnum.BEGIN.name());
        bringVerifyDestroySet.add(BringVerifyEnum.SHIPMENT_CREATE.name());
        bringVerifyDestroySet.add(BringVerifyEnum.FREEZE_INVENTORY.name());
        bringVerifyDestroySet.add(BringVerifyEnum.FREEZE_OPERATION.name());
        bringVerifyDestroySet.add(BringVerifyEnum.END.name());
        bringVerifyRuleMap.put(DelOutboundOrderTypeEnum.DESTROY.getCode(), bringVerifyDestroySet);
        // 提审配置 - 【自提出库】
        Set<String> bringVerifySelfPickSet = new HashSet<>();
        bringVerifySelfPickSet.add(BringVerifyEnum.BEGIN.name());
        bringVerifySelfPickSet.add(BringVerifyEnum.SHIPMENT_CREATE.name());
        bringVerifySelfPickSet.add(BringVerifyEnum.SHIPMENT_LABEL.name());
        bringVerifySelfPickSet.add(BringVerifyEnum.FREEZE_INVENTORY.name());
        bringVerifySelfPickSet.add(BringVerifyEnum.FREEZE_OPERATION.name());
        bringVerifySelfPickSet.add(BringVerifyEnum.END.name());
        bringVerifyRuleMap.put(DelOutboundOrderTypeEnum.SELF_PICK.getCode(), bringVerifySelfPickSet);
        // 提审配置 - 【转运出库】
        // 执行所有步骤
        // 提审配置 - 【集运出库】
        // 执行所有步骤
        // 提审配置 - 【新SKU上架出库】
        Set<String> bringVerifyNewSkuSet = new HashSet<>();
        bringVerifyNewSkuSet.add(BringVerifyEnum.BEGIN.name());
        bringVerifyNewSkuSet.add(BringVerifyEnum.SHIPMENT_CREATE.name());
        bringVerifyNewSkuSet.add(BringVerifyEnum.FREEZE_INVENTORY.name());
        bringVerifyNewSkuSet.add(BringVerifyEnum.FREEZE_OPERATION.name());
        bringVerifyNewSkuSet.add(BringVerifyEnum.END.name());
        bringVerifyRuleMap.put(DelOutboundOrderTypeEnum.NEW_SKU.getCode(), bringVerifyNewSkuSet);
        // 提审配置 - 【拆分SKU】
        Set<String> bringVerifySplitSkuSet = new HashSet<>();
        bringVerifySplitSkuSet.add(BringVerifyEnum.BEGIN.name());
        bringVerifySplitSkuSet.add(BringVerifyEnum.SHIPMENT_CREATE.name());
        bringVerifySplitSkuSet.add(BringVerifyEnum.FREEZE_INVENTORY.name());
        bringVerifySplitSkuSet.add(BringVerifyEnum.FREEZE_OPERATION.name());
        bringVerifySplitSkuSet.add(BringVerifyEnum.END.name());
        bringVerifyRuleMap.put(DelOutboundOrderTypeEnum.SPLIT_SKU.getCode(), bringVerifySplitSkuSet);
        // 提审配置 - 【批量出库】
        // 执行所有步骤
        // =======================================================================================
        // 出库配置
        // =======================================================================================
        // 没有配置默认执行所有步骤
        // 出库配置 - 【普通出库】
        // 执行所有步骤
        // 出库配置 - 【销毁出库】
        Set<String> shipmentDestroySet = new HashSet<>();
        shipmentDestroySet.add(ShipmentEnum.BEGIN.name());
        shipmentDestroySet.add(ShipmentEnum.END.name());
        shipmentRuleMap.put(DelOutboundOrderTypeEnum.DESTROY.getCode(), shipmentDestroySet);
        // 出库配置 - 【自提出库】
        Set<String> shipmentSelfPickSet = new HashSet<>();
        shipmentSelfPickSet.add(ShipmentEnum.BEGIN.name());
        shipmentSelfPickSet.add(ShipmentEnum.END.name());
        shipmentRuleMap.put(DelOutboundOrderTypeEnum.SELF_PICK.getCode(), shipmentSelfPickSet);
        // 出库配置 - 【转运出库】
        // 执行所有步骤
        // 出库配置 - 【集运出库】
        // 执行所有步骤
        // 出库配置 - 【新SKU上架出库】
        Set<String> shipmentNewSkuSet = new HashSet<>();
        shipmentNewSkuSet.add(ShipmentEnum.BEGIN.name());
        shipmentNewSkuSet.add(ShipmentEnum.SHIPMENT_SHIPPING.name());
        shipmentNewSkuSet.add(ShipmentEnum.END.name());
        shipmentRuleMap.put(DelOutboundOrderTypeEnum.NEW_SKU.getCode(), shipmentNewSkuSet);
        // 出库配置 - 【拆分SKU】
        Set<String> shipmentSplitSkuSet = new HashSet<>();
        shipmentSplitSkuSet.add(ShipmentEnum.BEGIN.name());
        shipmentSplitSkuSet.add(ShipmentEnum.SHIPMENT_SHIPPING.name());
        shipmentSplitSkuSet.add(ShipmentEnum.END.name());
        shipmentRuleMap.put(DelOutboundOrderTypeEnum.SPLIT_SKU.getCode(), shipmentSplitSkuSet);
        // 出库配置 - 【批量出库】
        // 执行所有步骤
    }

    /**
     * 提审 - 判断是否满足条件
     *
     * @param orderTypeEnum orderTypeEnum
     * @param currentState  currentState
     * @return boolean
     */
    public static boolean bringVerifyCondition(DelOutboundOrderTypeEnum orderTypeEnum, String currentState) {
        String code = orderTypeEnum.getCode();
        // 根据类型获取配置的规则
        // 如果没有配置规则，默认满足条件
        if (!bringVerifyRuleMap.containsKey(code)) {
            return true;
        }
        // 判断配置的规则是否存在
        return bringVerifyRuleMap.get(code).contains(currentState);
    }

    /**
     * 出库 - 判断是否满足条件
     *
     * @param orderTypeEnum orderTypeEnum
     * @param currentState  currentState
     * @return boolean
     */
    public static boolean shipmentCondition(DelOutboundOrderTypeEnum orderTypeEnum, String currentState) {
        String code = orderTypeEnum.getCode();
        // 不存在配置，默认所有节点都执行
        if (!shipmentRuleMap.containsKey(code)) {
            return true;
        }
        // 只执行配置的节点
        return shipmentRuleMap.get(code).contains(currentState);
    }
}
