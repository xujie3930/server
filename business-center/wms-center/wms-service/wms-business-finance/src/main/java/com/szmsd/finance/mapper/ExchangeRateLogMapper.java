package com.szmsd.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.finance.domain.ExchangeRateLog;

import java.util.List;

/**
 * @author weixiaofeng
 */
public interface ExchangeRateLogMapper extends BaseMapper<ExchangeRateLog> {
    int saveBatch(List<ExchangeRateLog> exchangeRateLogs);
}
