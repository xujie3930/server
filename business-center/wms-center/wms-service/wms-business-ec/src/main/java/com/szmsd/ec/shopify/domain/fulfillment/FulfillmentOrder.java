package com.szmsd.ec.shopify.domain.fulfillment;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class FulfillmentOrder {

    @JSONField(name = "fulfillment_order_id")
    private String fulfillmentOrderId;

    @JSONField(name = "fulfillment_order_line_items")
    private List<LineItem> lineItemList;
}
