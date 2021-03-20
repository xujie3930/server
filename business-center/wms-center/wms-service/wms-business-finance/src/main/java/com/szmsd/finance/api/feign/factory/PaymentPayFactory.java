package com.szmsd.finance.api.feign.factory;

import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.api.feign.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.mapper.AccountBalanceChangeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author liulei
 */
@Component
public class PaymentPayFactory extends AbstractPayFactory {

    @Autowired
    AccountBalanceChangeMapper accountBalanceChangeMapper;

    @Autowired
    SerialNumberClientService serialNumberClientService;

    @Override
    protected void setOpLogAmount(AccountBalanceChange accountBalanceChange, BigDecimal amount) {
        accountBalanceChange.setAmountChange(amount.negate());

    }

    @Override
    public BigDecimal calculateBalance(BigDecimal oldBalance, BigDecimal changeAmount) {
        return oldBalance.subtract(changeAmount);
    }
}
