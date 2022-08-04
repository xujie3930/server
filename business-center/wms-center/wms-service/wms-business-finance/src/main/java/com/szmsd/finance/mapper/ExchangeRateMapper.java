package com.szmsd.finance.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.finance.domain.ExchangeRate;
import com.szmsd.finance.dto.ExchangeRateDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author liulei
 */
public interface ExchangeRateMapper extends BaseMapper<ExchangeRate> {
    List<ExchangeRate> listPage(@Param(Constants.WRAPPER)LambdaQueryWrapper queryWrapper);

    List<ExchangeRate> checkExchangeRateIsExists(@Param("dto") ExchangeRateDTO dto);

    List<ExchangeRateDTO>   selectRates(@Param("map") Map map);

    void deleteExchangeRate(@Param("map") Map map);
}
