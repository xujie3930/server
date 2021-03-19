package com.szmsd.chargerules.factory;

import org.springframework.stereotype.Component;

/**
 * 出库
 */
@Component
public class Shipment extends OrderType {

    @Override
    public boolean checkOrderExist(String orderNo) {
        return false;
    }

}
