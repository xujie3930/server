package com.szmsd.finance.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.domain.AccountBalanceChange;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liulei
 */
public interface AccountBalanceChangeMapper extends BaseMapper<AccountBalanceChange> {

    List<AccountBalanceChange> recordListPage(@Param(Constants.WRAPPER) LambdaQueryWrapper queryWrapper);
}
