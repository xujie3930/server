package com.szmsd.ec.common.listener;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sun.org.apache.bcel.internal.generic.NEW;
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

//    @Autowired
//    private ExpressFeignService expressFeignService;

    @Async
    @EventListener
    public void onApplicationEvent(ShopifyFulfillmentEvent shopifyFulfillmentEvent){
        Object source = shopifyFulfillmentEvent.getSource();
        if (source == null) {
            return;
        }
        CommonOrder commonOrder = (CommonOrder) source;
        List<CommonOrderItem> commonOrderItems = commonOrderItemService.list(new LambdaQueryWrapper<CommonOrderItem>().eq(CommonOrderItem::getOrderId, commonOrder.getId()));
        // 判断该订单是否走了fedex 下单
//        R<Express> expressR = expressFeignService.getByOrderCode(commonOrder.getOrderNo());
//        if (expressR == null || !Constants.SUCCESS.equals(expressR.getCode())){
//            log.info("该订单未通过FedEx 物流下单, 不走创建履约单逻辑");
//            return;
//        }
//        Express express = expressR.getData();
//        if (!express.getCarrierName().equalsIgnoreCase("Fedex")) {
//            log.info("该订单未通过FedEx 物流下单, 不走创建履约单逻辑");
//            return;
//        }
        CreateFulfillmentReqeust reqeust = new CreateFulfillmentReqeust();
        Fulfillment2 ft = new Fulfillment2();
        ft.setNotifyCustomer(false);

        // todo: 履约快递信息需要更改
//        ft.setTrackingNumber(express.getExpressCode());
//        ft.setTrackingCompany("FedEx");

        List<LineItem> items = new ArrayList<>();
        for (CommonOrderItem item : commonOrderItems) {
            LineItem lineItem = new LineItem();
            lineItem.setId(item.getItemId());
            lineItem.setQuantity(item.getQuantity());
            items.add(lineItem);
        }
        ft.setLineItemList(items);
//        Fulfillment fulfillment = new Fulfillment();
//        fulfillment.setMessage("The package has been shipped.");
//        fulfillment.setNotifyCustomer(false);
//        fulfillment.setTrackingInfo(new FulfillmentTrackingInfo(express.getExpressCode(), "", "Fedex"));
//
//        // 设置订单信息
//        List<FulfillmentOrder> orders = new ArrayList<>();
//        FulfillmentOrder order = new FulfillmentOrder();
//        order.setFulfillmentOrderId(commonOrder.getOrderNo());
//
//        // 设置订单详情
//        List<LineItem> items = new ArrayList<>();
//        for (CommonOrderItem item : commonOrderItems) {
//            LineItem lineItem = new LineItem();
//            lineItem.setId(item.getItemId());
//            lineItem.setQuantity(item.getQuantity());
//            items.add(lineItem);
//        }
//        order.setLineItemList(items);
//        orders.add(order);
//        fulfillment.setFulfillmentOrders(orders);
        reqeust.setFulfillment(ft);
        JSONObject fulfillmentResult = shopifyOrderService.createFulfillment(commonOrder.getShopName(),commonOrder.getOrderNo(), reqeust);
        if (fulfillmentResult == null || fulfillmentResult.containsKey("error")){
            commonOrder.setFulfillmentStatus("0");
            log.info("履约单创建失败!!");
        } else if (fulfillmentResult != null) {
            JSONObject fulfillment = fulfillmentResult.getJSONObject("fulfillment");
            commonOrder.setFulfillmentStatus("success".equalsIgnoreCase(fulfillment.getString("status")) ? "1" : "0");
            commonOrder.setFulfillmentId(fulfillment.get("id").toString());
            commonOrder.setLocationId(fulfillment.get("location_id").toString());
            log.info("履约单创建成功!!");
        }
        commonOrderService.updateById(commonOrder);

    }
}
