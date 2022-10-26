package com.szmsd.http.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DirectExpressOrderApiDTO implements Serializable {

    private Integer weightInit;

    private DirectExpressOrderPack packingInit;

    private Integer weight;

    private DirectExpressOrderPack packing;

    private Integer chargedWeight;

    private String packageId;

    private String ck1PackageId;

    private String status;

    private String handleStatus;

    private String trackingNumber;

    private String extraTrackNumber;

    private String isFinalTrackingNumber;

    private String shippingProvider;

    private List<DirectExpressOrderShippingCost> shippingCosts;

    private String hasRemoteFee;

    private String warehouseReceivedTime;

    private String warehouseDepartedTime;


}
