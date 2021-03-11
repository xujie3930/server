package com.szmsd.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.common.core.domain.R;
import com.szmsd.finance.domain.FssExchangeRate;
import com.szmsd.finance.dto.FssExchangeRateDTO;

import java.util.List;

/**
 * @author liulei
 */
public interface IExchangeRateService {
    List<FssExchangeRate> listPage(FssExchangeRateDTO dto);

    R save(FssExchangeRateDTO dto);

    R update(FssExchangeRateDTO dto);
}
