package com.szmsd.finance.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.dto.*;
import com.szmsd.finance.vo.UserCreditInfoVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liulei
 */
public interface IAccountBalanceService {
    List<AccountBalance> listPage(AccountBalanceDTO dto);

    List<AccountBalanceChange> recordListPage(AccountBalanceChangeDTO dto);

    R onlineIncome(CustPayDTO dto);

    R refund(CustPayDTO dto);

    R offlineIncome(CustPayDTO dto);

    R balanceExchange(CustPayDTO dto);

    BigDecimal getCurrentBalance(String cusCode, String currencyCode);

    void setCurrentBalance(String cusCode, String currencyCode, BigDecimal result);

    R withdraw(CustPayDTO dto);

    R preOnlineIncome(CustPayDTO dto);

    R rechargeCallback(RechargesCallbackRequestDTO requestDTO);

    R warehouseFeeDeductions(CustPayDTO dto);

    R feeDeductions(CustPayDTO dto);

    R freezeBalance(CusFreezeBalanceDTO dto);

    R thawBalance(CusFreezeBalanceDTO dto);

    BalanceDTO getBalance(String cusCode, String currencyCode);

    void setBalance(String cusCode, String currencyCode, BalanceDTO result, boolean needUpdateCredit);

    boolean withDrawBalanceCheck(String cusCode, String currencyCode, BigDecimal amount);

    int updateAccountBalanceChange(AccountBalanceChangeDTO dto);

    void updateCreditStatus(CustPayDTO dto);

    void updateUserCredit(UserCreditDTO userCreditDTO);

    List<UserCreditInfoVO> queryUserCredit(String cusCode);

    /**
     * 查询授信期更新的用户列表
     *
     * @return
     */
    List<AccountBalance> queryAndUpdateUserCreditTimeFlag();

    /**
     * 查询授信额度临期的用户
     *
     * @return
     */
    List<AccountBalance> queryThePreTermBill();

    /**
     * 重新刷新账单周期
     *
     * @param cusCodeList 客户编号
     * @return
     */
    int reloadCreditTime(List<String> cusCodeList, String currencyCode);

    /**
     * 查询账期类型的用户并查询出已归还账期内的用户更新授信账单周期
     * @return
     */
    List<AccountBalance> queryTheCanUpdateCreditUserList();

    /**
     * 更新可以更新用户的账期时间
     */
    void updateUserCreditTime();

}
