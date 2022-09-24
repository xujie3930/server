package com.szmsd.finance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.finance.domain.AccountBalanceLog;
import com.szmsd.finance.mapper.AccountBalanceLogMapper;
import com.szmsd.finance.service.IAccountBalanceLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author liulei
 */
@Service
@Slf4j
public class AccountBalanceLogServiceImpl extends ServiceImpl<AccountBalanceLogMapper, AccountBalanceLog> implements IAccountBalanceLogService {


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoGeneratorBalance() {

        baseMapper.autoGeneratorBalance();

    }
}
