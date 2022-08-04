package com.szmsd.finance.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.finance.domain.ExchangeRate;
import com.szmsd.finance.dto.ExchangeRateDTO;

import java.util.List;
import java.util.Map;

/**
 * @author liulei
 */
public interface IExchangeRateService {
    List<ExchangeRate> listPage(ExchangeRateDTO dto);

    R save(ExchangeRateDTO dto);

    R update(ExchangeRateDTO dto);

    R delete(Long id);

    R selectRate(String currencyFromCode, String currencyToCode);


    List<ExchangeRateDTO>  selectRates(Map map);

    int insertExchangeRate(List<Map> mapList);

    void  deleteExchangeRate(Map map);
}
