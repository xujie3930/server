package com.szmsd.chargerules.api.service.impl;

import com.szmsd.chargerules.api.feign.ChargeFeignService;
import com.szmsd.chargerules.api.service.ChargeClientService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class ChargeServiceImpl implements ChargeClientService {

    @Resource
    private ChargeFeignService chargeFeignService;

    @Override
    public R<TableDataInfo<QueryChargeVO>> selectPage(QueryChargeDto queryDto) {
        return chargeFeignService.selectPage(queryDto);
    }

}
