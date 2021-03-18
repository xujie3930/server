package com.szmsd.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.dto.AccountBalanceChangeDTO;
import com.szmsd.finance.dto.AccountBalanceDTO;
import com.szmsd.finance.dto.BalanceExchangeDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.factory.abstractFactory.PayFactoryBuilder;
import com.szmsd.finance.mapper.AccountBalanceChangeMapper;
import com.szmsd.finance.mapper.AccountBalanceMapper;
import com.szmsd.finance.service.IAccountBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liulei
 */
@Service
public class AccountBalanceServiceImpl implements IAccountBalanceService {

    @Autowired
    PayFactoryBuilder payFactoryBuilder;

    @Autowired
    AccountBalanceMapper accountBalanceMapper;

    @Autowired
    AccountBalanceChangeMapper accountBalanceChangeMapper;

    @Override
    public List<AccountBalance> listPage(AccountBalanceDTO dto) {
        LambdaQueryWrapper<AccountBalance> queryWrapper = Wrappers.lambdaQuery();
        if(dto.getCusId()!=null) {
            queryWrapper.eq(AccountBalance::getCusId, dto.getCusId());
        }
        return accountBalanceMapper.listPage(queryWrapper);
    }

    @Override
    public List<AccountBalanceChange> recordListPage(AccountBalanceChangeDTO dto) {
        LambdaQueryWrapper<AccountBalanceChange> queryWrapper = Wrappers.lambdaQuery();
        if(dto.getCusId()!=null) {
            queryWrapper.eq(AccountBalanceChange::getCusId, dto.getCusId());
        }
        if(dto.getPayMethod()!=null) {
            queryWrapper.eq(AccountBalanceChange::getPayMethod, dto.getPayMethod());
        }
        if(StringUtils.isNotEmpty(dto.getBeginTime())){
            queryWrapper.ge(AccountBalanceChange::getCreateTime,dto.getBeginTime());
        }
        if(StringUtils.isNotEmpty(dto.getEndTime())){
            queryWrapper.le(AccountBalanceChange::getCreateTime,dto.getEndTime());
        }
        return accountBalanceChangeMapper.recordListPage(queryWrapper);
    }

    /**
     * 线上充值
     * @param loginUser
     * @param dto
     * @return
     */
    @Override
    public R onlineIncome(LoginUser loginUser, CustPayDTO dto) {
        fillCustInfo(loginUser,dto);
        dto.setPayType(BillEnum.PayType.INCOME);
        dto.setPayMethod(BillEnum.PayMethod.ONLINE_INCOME);
        AbstractPayFactory abstractPayFactory=payFactoryBuilder.build(dto.getPayType());
        boolean flag=abstractPayFactory.updateBalance(dto);
        return flag?R.ok():R.failed();
    }

    /**
     * 线下充值
     * @param loginUser
     * @param dto
     * @return
     */
    @Override
    public R offlineIncome(LoginUser loginUser, CustPayDTO dto) {
        fillCustInfo(loginUser,dto);
        dto.setPayType(BillEnum.PayType.INCOME);
        dto.setPayMethod(BillEnum.PayMethod.OFFLINE_INCOME);
        AbstractPayFactory abstractPayFactory=payFactoryBuilder.build(dto.getPayType());
        boolean flag=abstractPayFactory.updateBalance(dto);
        return flag?R.ok():R.failed();
    }

    /**
     * 余额汇率转换
     * @param loginUser
     * @param dto
     * @return
     */
    @Override
    public R balanceExchange(LoginUser loginUser, CustPayDTO dto) {
        fillCustInfo(loginUser,dto);
        dto.setPayType(BillEnum.PayType.EXCHANGE);
        AbstractPayFactory abstractPayFactory=payFactoryBuilder.build(dto.getPayType());
        boolean flag=abstractPayFactory.updateBalance(dto);
        return flag?R.ok():R.failed();
    }

    /**
     * 查询币种余额，如果不存在初始化
     * @param custId
     * @param currencyId
     * @return
     */
    @Override
    public BigDecimal getCurrentBalance(Long custId,Long currencyId) {
        QueryWrapper<AccountBalance> queryWrapper = new QueryWrapper();
        queryWrapper.eq("cus_id",custId);
        queryWrapper.eq("currency_id",currencyId);
        AccountBalance accountBalance = accountBalanceMapper.selectOne(queryWrapper);
        if(accountBalance!=null){
            return accountBalance.getCurrentBalance();
        }
        accountBalance=new AccountBalance(custId,currencyId,getCurrencyName(currencyId),BigDecimal.ZERO);
        accountBalanceMapper.insert(accountBalance);
        return BigDecimal.ZERO;
    }

    /**
     * 更新币种余额
     * @param cusId
     * @param currencyId
     * @param result
     */
    @Override
    public void setCurrentBalance(Long cusId, Long currencyId, BigDecimal result) {
        LambdaUpdateWrapper<AccountBalance> lambdaUpdateWrapper=Wrappers.lambdaUpdate();
        lambdaUpdateWrapper.eq(AccountBalance::getCusId,cusId);
        lambdaUpdateWrapper.eq(AccountBalance::getCurrencyId,currencyId);
        lambdaUpdateWrapper.set(AccountBalance::getCurrentBalance,result);
        accountBalanceMapper.update(null,lambdaUpdateWrapper);
    }

    /**
     * 提现
     * @param loginUser
     * @param dto
     * @return
     */
    @Override
    public R withdraw(LoginUser loginUser, CustPayDTO dto) {
        fillCustInfo(loginUser,dto);
        dto.setPayType(BillEnum.PayType.PAYMENT);
        dto.setPayMethod(BillEnum.PayMethod.WITHDRAW_PAYMENT);
        AbstractPayFactory abstractPayFactory=payFactoryBuilder.build(dto.getPayType());
        boolean flag=abstractPayFactory.updateBalance(dto);
        return flag?R.ok():R.failed();
    }

    private String getCurrencyName(Long currencyId) {
        //for test
        return "人民币";
    }

    /**
     * 填充客户信息
     * @param loginUser
     * @param payment
     */
    private void fillCustInfo(LoginUser loginUser, CustPayDTO payment) {
        if(loginUser!=null){
            payment.setCusId(loginUser.getUserId());
            payment.setCusCode("100015");
            payment.setCusName(loginUser.getUsername());
        }
        //for test
        payment.setCusId(100015l);
        payment.setCusCode("100015");
        payment.setCusName("Sunder");
    }
}
