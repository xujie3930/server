package com.szmsd.finance.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.dto.AccountBalanceChangeDTO;
import com.szmsd.finance.dto.AccountBalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.dto.RechargesCallbackRequestDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liulei
 */
public interface IAccountBalanceService {
    List<AccountBalance> listPage(AccountBalanceDTO dto);

    List<AccountBalanceChange> recordListPage(AccountBalanceChangeDTO dto);

    R onlineIncome(CustPayDTO dto);

    R offlineIncome(CustPayDTO dto);

    R balanceExchange(CustPayDTO dto);

    BigDecimal getCurrentBalance(String cusCode,String currencyCode);

    void setCurrentBalance(String cusCode, String currencyCode, BigDecimal result);

    R withdraw(CustPayDTO dto);

    R preOnlineIncome(CustPayDTO dto);

    R rechargeCallback(RechargesCallbackRequestDTO requestDTO);

    R warehouseFeeDeductions(CustPayDTO dto);
}
