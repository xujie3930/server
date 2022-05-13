package com.szmsd.ec.shopify.domain.fulfillment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FulfillmentTrackingInfo {

    private String number;

    private String url;

    private String company;

}
