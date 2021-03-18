package com.szmsd.finance.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.finance.domain.AccountBalance;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liulei
 */
public interface AccountBalanceMapper extends BaseMapper<AccountBalance> {
    List<AccountBalance> listPage(@Param(Constants.WRAPPER) LambdaQueryWrapper queryWrapper);

}
