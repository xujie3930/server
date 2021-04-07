package com.szmsd.chargerules.factory;

import com.szmsd.chargerules.vo.OperationVo;
import org.springframework.stereotype.Component;

/**
 * 退货
 */
@Component
public class Bounce extends OrderType {

    @Override
    public String findOrderById(String orderNo) {
        return null;
    }

    @Override
    public void operationPay(OperationVo operation) {

    }

}
