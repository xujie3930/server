package com.szmsd.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.finance.domain.AccountBalanceLog;
import com.szmsd.finance.dto.AccountBalanceBillResultDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author liulei
 */
public interface AccountBalanceLogMapper extends BaseMapper<AccountBalanceLog> {


    int autoGeneratorBalance();

    List<AccountBalanceBillResultDTO> cusPeriodAmount(@Param("cusCodeList") List<String> cusCodeList, @Param("generatorTime") Date generatorTime);
}
