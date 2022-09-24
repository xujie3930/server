package com.szmsd.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.finance.domain.AccountBalanceLog;

/**
 * @author liulei
 */
public interface AccountBalanceLogMapper extends BaseMapper<AccountBalanceLog> {


    int autoGeneratorBalance();
}
