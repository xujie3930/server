package com.szmsd.finance.factory.abstractFactory;

import com.google.common.collect.ImmutableMap;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.factory.ExchangePayFactory;
import com.szmsd.finance.factory.IncomePayFactory;
import com.szmsd.finance.factory.PaymentPayFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author liulei
 */
@Component
public class PayFactoryBuilder {
    @Autowired
    private IncomePayFactory incomePayFactory;

    @Autowired
    private PaymentPayFactory paymentPayFactory;

    @Autowired
    private ExchangePayFactory exchangePayFactory;

    private ImmutableMap<BillEnum.PayType, AbstractPayFactory> factoryMap;

    @PostConstruct
    private void factoryMapInit(){
        factoryMap = new ImmutableMap.Builder<BillEnum.PayType,AbstractPayFactory>()
                .put(BillEnum.PayType.INCOME,incomePayFactory)
                .put(BillEnum.PayType.PAYMENT,paymentPayFactory)
                .put(BillEnum.PayType.EXCHANGE,exchangePayFactory)
                .build();

    }

    public AbstractPayFactory build(BillEnum.PayType payType){
        return factoryMap.get(payType);
    }

}
