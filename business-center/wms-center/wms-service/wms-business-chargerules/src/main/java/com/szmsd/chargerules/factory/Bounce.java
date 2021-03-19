package com.szmsd.chargerules.factory;

import org.springframework.stereotype.Component;

/**
 * 退货
 */
@Component
public class Bounce extends OrderType {

    @Override
    public boolean checkOrderExist(String orderNo) {
        return false;
    }

}
