package com.szmsd.ec.shopify.domain.fulfillment;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fulfillment2 extends BaseFulfillment {

    @JSONField(name = "location_id")
    private String locationId;

    @JSONField(name = "tracking_company")
    private String trackingCompany;

    @JSONField(name = "tracking_number")
    private String trackingNumber;

    @JSONField(name = "notify_customer")
    private Boolean notifyCustomer;

    @JSONField(name = "line_items")
    private List<LineItem> lineItemList;
}
