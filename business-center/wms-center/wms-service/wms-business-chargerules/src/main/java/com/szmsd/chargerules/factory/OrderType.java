package com.szmsd.chargerules.factory;

import com.szmsd.chargerules.vo.OperationVo;

public abstract class OrderType {

    public abstract String findOrderById(String orderNo);

    public abstract void operationPay(OperationVo operation);

}
