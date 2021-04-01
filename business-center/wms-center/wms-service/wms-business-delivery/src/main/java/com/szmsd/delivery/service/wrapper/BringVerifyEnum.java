package com.szmsd.delivery.service.wrapper;

import com.szmsd.delivery.domain.DelOutbound;

import java.util.HashMap;
import java.util.Map;

/**
 * 出库单提审步骤
 *
 * @author zhangyuyuan
 * @date 2021-04-01 16:08
 */
public enum BringVerifyEnum implements ApplicationState, ApplicationRegister {

    /**
     * 开始
     */
    BEGIN,

    // #1 PRC 计费
    PRC_PRICING,

    // #2 冻结费用
    FREEZE_BALANCE,

    // #3 获取产品信息
    PRODUCT_INFO,

    // #4 新增/修改发货规则
    SHIPMENT_RULE,

    // #5 创建承运商物流订单
    SHIPMENT_ORDER,

    // #6 推单WMS
    SHIPMENT_CREATE,

    /**
     * 结束
     */
    END,
    ;

    public static BringVerifyEnum get(String name) {
        for (BringVerifyEnum anEnum : BringVerifyEnum.values()) {
            if (anEnum.name().equals(name)) {
                return anEnum;
            }
        }
        return null;
    }

    @Override
    public Map<String, ApplicationHandle> register() {
        Map<String, ApplicationHandle> map = new HashMap<>();
        map.put(BEGIN.name(), new BeginHandle());
        map.put(PRC_PRICING.name(), new PrcPricingHandle());
        map.put(FREEZE_BALANCE.name(), new FreezeBalanceHandle());
        map.put(PRODUCT_INFO.name(), new ProductInfoHandle());
        map.put(SHIPMENT_RULE.name(), new ShipmentRuleHandle());
        map.put(SHIPMENT_ORDER.name(), new ShipmentOrderHandle());
        map.put(SHIPMENT_CREATE.name(), new ShipmentCreateHandle());
        map.put(END.name(), new EndHandle());
        return map;
    }

    static class BeginHandle implements ApplicationHandle {

        @Override
        public ApplicationState quoState() {
            return BEGIN;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext wrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = wrapperContext.getDelOutbound();

        }

        @Override
        public ApplicationState nextState() {
            return PRC_PRICING;
        }
    }

    static class PrcPricingHandle implements ApplicationHandle {

        @Override
        public ApplicationState quoState() {
            return null;
        }

        @Override
        public void handle(ApplicationContext context) {

        }

        @Override
        public ApplicationState nextState() {
            return null;
        }
    }

    static class FreezeBalanceHandle implements ApplicationHandle {

        @Override
        public ApplicationState quoState() {
            return null;
        }

        @Override
        public void handle(ApplicationContext context) {

        }

        @Override
        public ApplicationState nextState() {
            return null;
        }
    }

    static class ProductInfoHandle implements ApplicationHandle {

        @Override
        public ApplicationState quoState() {
            return null;
        }

        @Override
        public void handle(ApplicationContext context) {

        }

        @Override
        public ApplicationState nextState() {
            return null;
        }
    }

    static class ShipmentRuleHandle implements ApplicationHandle {

        @Override
        public ApplicationState quoState() {
            return null;
        }

        @Override
        public void handle(ApplicationContext context) {

        }

        @Override
        public ApplicationState nextState() {
            return null;
        }
    }

    static class ShipmentOrderHandle implements ApplicationHandle {

        @Override
        public ApplicationState quoState() {
            return null;
        }

        @Override
        public void handle(ApplicationContext context) {

        }

        @Override
        public ApplicationState nextState() {
            return null;
        }
    }

    static class ShipmentCreateHandle implements ApplicationHandle {

        @Override
        public ApplicationState quoState() {
            return null;
        }

        @Override
        public void handle(ApplicationContext context) {

        }

        @Override
        public ApplicationState nextState() {
            return null;
        }
    }

    static class EndHandle implements ApplicationHandle {

        @Override
        public ApplicationState quoState() {
            return END;
        }

        @Override
        public void handle(ApplicationContext context) {

        }

        @Override
        public ApplicationState nextState() {
            return END;
        }
    }
}
