package com.szmsd.finance.factory.abstractFactory;

import com.google.common.collect.ImmutableMap;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.factory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author liulei
 */
@Component
public class PayFactoryBuilder {
    @Autowired
    private IncomePayFactory incomePayFactory;

    @Autowired
    private RefundPayFactory refundPayFactory;

    @Autowired
    private PaymentPayFactory paymentPayFactory;

    @Autowired
    private ExchangePayFactory exchangePayFactory;

    @Autowired
    private BalanceFreezeFactory balanceFreezeFactory;

    @Resource
    private PaymentNoFreezePayFactory paymentNoFreezePayFactory;

    private ImmutableMap<BillEnum.PayType, AbstractPayFactory> factoryMap;

    @PostConstruct
    private void factoryMapInit(){
        factoryMap = new ImmutableMap.Builder<BillEnum.PayType,AbstractPayFactory>()
                .put(BillEnum.PayType.INCOME,incomePayFactory)
                .put(BillEnum.PayType.PAYMENT,paymentPayFactory)
                .put(BillEnum.PayType.EXCHANGE,exchangePayFactory)
                .put(BillEnum.PayType.FREEZE,balanceFreezeFactory)
                .put(BillEnum.PayType.PAYMENT_NO_FREEZE,paymentNoFreezePayFactory)
                .put(BillEnum.PayType.REFUND,refundPayFactory)
                .build();

    }

    public AbstractPayFactory build(BillEnum.PayType payType){
        return factoryMap.get(payType);
    }

}
