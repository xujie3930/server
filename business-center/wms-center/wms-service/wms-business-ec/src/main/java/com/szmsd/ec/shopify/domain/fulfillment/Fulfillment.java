package com.szmsd.ec.shopify.domain.fulfillment;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fulfillment extends BaseFulfillment {

    private String message;

    @JSONField(name = "notify_customer")
    private Boolean notifyCustomer;

    @JSONField(name = "tracking_info")
    private FulfillmentTrackingInfo trackingInfo;

    @JSONField(name = "line_items_by_fulfillment_order")
    private List<FulfillmentOrder> fulfillmentOrders;
}
