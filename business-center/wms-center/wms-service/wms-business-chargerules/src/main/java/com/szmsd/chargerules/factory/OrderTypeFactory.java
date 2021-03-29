package com.szmsd.chargerules.factory;

import com.google.common.collect.ImmutableMap;
import com.szmsd.chargerules.enums.OrderTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class OrderTypeFactory {

    private ImmutableMap<String, OrderType> map;

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
        map = new ImmutableMap.Builder<String, OrderType>()
                .put(OrderTypeEnum.Shipment.getNameEn(), shipment)
                .put(OrderTypeEnum.Receipt.getNameEn(), receipt)
                .put(OrderTypeEnum.Bounce.getNameEn(), bounce)
                .build();
    }

}
