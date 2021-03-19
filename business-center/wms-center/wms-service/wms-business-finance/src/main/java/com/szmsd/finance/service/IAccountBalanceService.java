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

    R onlineIncome(LoginUser loginUser, CustPayDTO dto);

    R offlineIncome(LoginUser loginUser, CustPayDTO dto);

    R balanceExchange(LoginUser loginUser, CustPayDTO dto);

    BigDecimal getCurrentBalance(Long cusId,Long currencyId);

    void setCurrentBalance(Long cusId, Long currencyId, BigDecimal result);

    R withdraw(LoginUser loginUser, CustPayDTO dto);

    R preOnlineIncome(CustPayDTO dto);

    R rechargeCallback(RechargesCallbackRequestDTO requestDTO);
}
