package com.szmsd.chargerules.factory;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class OrderTypeFactory {

    //出库
    public static final String Shipment = "Shipment";

    //入库
    public static final String Receipt = "Receipt";

    //退件
    public static final String Bounce = "Bounce";

    private ImmutableMap<String,OrderType> map;

    @Resource
    private Receipt receipt;

    @Resource
    private Shipment shipment;

    @Resource
    private Bounce bounce;

    public OrderType getFactory(String type) {
        return map.get(type);
    }

    @PostConstruct
    public void construct() {
        map = new ImmutableMap.Builder<String,OrderType>().put(Shipment,shipment).put(Receipt,receipt).put(Bounce,bounce).build();
    }

}
