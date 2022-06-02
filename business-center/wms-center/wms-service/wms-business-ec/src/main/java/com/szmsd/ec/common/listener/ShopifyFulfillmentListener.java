package com.szmsd.ec.common.listener;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.szmsd.bas.api.feign.BasSkuRuleMatchingFeignService;
import com.szmsd.bas.domain.BasOtherRules;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.ec.common.event.ShopifyFulfillmentEvent;
import com.szmsd.ec.common.service.ICommonOrderItemService;
import com.szmsd.ec.common.service.ICommonOrderService;
import com.szmsd.ec.domain.CommonOrder;
import com.szmsd.ec.domain.CommonOrderItem;
import com.szmsd.ec.shopify.domain.fulfillment.CreateFulfillmentReqeust;
import com.szmsd.ec.shopify.domain.fulfillment.Fulfillment2;
import com.szmsd.ec.shopify.domain.fulfillment.LineItem;
import com.szmsd.ec.shopify.service.ShopifyOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * shopify 履约单创建
 */
@Slf4j
@Component
public class ShopifyFulfillmentListener {

    @Autowired
    private ICommonOrderItemService commonOrderItemService;

    @Autowired
    private ICommonOrderService commonOrderService;

    @Autowired
    private ShopifyOrderService shopifyOrderService;

    @Autowired
    private BasSkuRuleMatchingFeignService basSkuRuleMatchingFeignService;

    /**
     * 业务单号（OMS出库单号）
     */
    private static final String RETURN_RULE_105001 = "105001";

    /**
     * 跟踪号
     */
    private static final String RETURN_RULE_105002 = "105002";

    @Async
    @EventListener
    public void onApplicationEvent(ShopifyFulfillmentEvent shopifyFulfillmentEvent){
        log.info("【Shopify】履约单操作开始!");
        Object source = shopifyFulfillmentEvent.getSource();
        if (source == null) {
            return;
        }
        CommonOrder commonOrder = (CommonOrder) source;
        List<CommonOrderItem> commonOrderItems = commonOrderItemService.list(new LambdaQueryWrapper<CommonOrderItem>().eq(CommonOrderItem::getOrderId, commonOrder.getId()));
        CreateFulfillmentReqeust reqeust = new CreateFulfillmentReqeust();
        Fulfillment2 ft = new Fulfillment2();
        ft.setNotifyCustomer(false);

        // 默认按照跟踪号发货
        String returnRule = RETURN_RULE_105002;
        R<BasOtherRules> info = basSkuRuleMatchingFeignService.getInfo(commonOrder.getCusCode());
        if (Constants.SUCCESS.equals(info.getCode()) && info.getData() != null) {
            BasOtherRules rules = info.getData();
            returnRule = rules.getReturnRule();
        }
        log.info("物流号填充规则：{}", returnRule);
        if (RETURN_RULE_105002.equalsIgnoreCase(returnRule)) {
            ft.setTrackingNumber(commonOrder.getTransferNumber());
        }else if (RETURN_RULE_105001.equalsIgnoreCase(returnRule)){
            ft.setTrackingNumber(commonOrder.getOmsOrderNo());
        }

        ft.setTrackingCompany(commonOrder.getLogisticsRouteId());

        List<LineItem> items = new ArrayList<>();
        for (CommonOrderItem item : commonOrderItems) {
            LineItem lineItem = new LineItem();
            lineItem.setId(item.getItemId());
            lineItem.setQuantity(item.getQuantity());
            items.add(lineItem);
        }
        ft.setLocationId(commonOrder.getShippingWarehouseId() + "");
        ft.setLineItemList(items);
        reqeust.setFulfillment(ft);
        JSONObject fulfillmentResult = shopifyOrderService.createFulfillment(commonOrder.getShopName(),commonOrder.getOrderNo(), reqeust);
        log.info("【Shopify】履约单创建结果： {}", fulfillmentResult.toJSONString());
        if (fulfillmentResult == null || fulfillmentResult.containsKey("error")){
            commonOrder.setFulfillmentStatus("0");
            log.info("【Shopify】履约单创建失败!!");
        } else if (fulfillmentResult != null) {
            JSONObject fulfillment = fulfillmentResult.getJSONObject("fulfillment");
            commonOrder.setFulfillmentStatus("success".equalsIgnoreCase(fulfillment.getString("status")) ? "1" : "0");
            commonOrder.setFulfillmentId(fulfillment.get("id").toString());
            commonOrder.setLocationId(fulfillment.get("location_id").toString());
            log.info("【Shopify】履约单创建成功!!");
        }
        commonOrderService.updateById(commonOrder);
        log.info("【Shopify】履约单操作完毕!");
    }
}
