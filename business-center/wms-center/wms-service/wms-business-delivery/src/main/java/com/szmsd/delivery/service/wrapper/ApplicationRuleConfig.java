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
        // 提审配置 - 销毁出库
        Set<String> bringVerifyDestroySet = new HashSet<>();
        bringVerifyDestroySet.add(BringVerifyEnum.BEGIN.name());
        bringVerifyDestroySet.add(BringVerifyEnum.SHIPMENT_CREATE.name());
        bringVerifyDestroySet.add(BringVerifyEnum.END.name());
        bringVerifyRuleMap.put(DelOutboundOrderTypeEnum.DESTROY.getCode(), bringVerifyDestroySet);
        // 出库配置 - 销毁出库
        Set<String> shipmentDestroySet = new HashSet<>();
        shipmentDestroySet.add(ShipmentEnum.BEGIN.name());
        shipmentDestroySet.add(ShipmentEnum.END.name());
        shipmentRuleMap.put(DelOutboundOrderTypeEnum.DESTROY.getCode(), shipmentDestroySet);
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
        if (!bringVerifyRuleMap.containsKey(code)) {
            return true;
        }
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
