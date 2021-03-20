package com.szmsd.finance.factory;

import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.mapper.AccountBalanceChangeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author liulei
 */
@Component
public class IncomePayFactory extends AbstractPayFactory {

    @Autowired
    AccountBalanceChangeMapper accountBalanceChangeMapper;

    @Autowired
    SerialNumberClientService serialNumberClientService;

    @Override
    protected void setOpLogAmount(AccountBalanceChange accountBalanceChange, BigDecimal amount) {
        accountBalanceChange.setAmountChange(amount);
    }

    @Override
    public BigDecimal calculateBalance(BigDecimal oldBalance, BigDecimal changeAmount) {
        return oldBalance.add(changeAmount);
    }
}
