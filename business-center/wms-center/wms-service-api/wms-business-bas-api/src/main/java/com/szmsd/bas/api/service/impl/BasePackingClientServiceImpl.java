package com.szmsd.bas.api.service.impl;

import com.szmsd.bas.api.feign.BasePackingFeignService;
import com.szmsd.bas.api.service.BasePackingClientService;
import com.szmsd.bas.domain.BasePacking;
import com.szmsd.bas.dto.BasePackingConditionQueryDto;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-06 16:56
 */
@Service
public class BasePackingClientServiceImpl implements BasePackingClientService {

    @Autowired
    private BasePackingFeignService basePackingFeignService;

    @Override
    public List<BasePacking> queryPackingList(BaseProductConditionQueryDto conditionQueryDto) {
        return R.getDataAndException(this.basePackingFeignService.queryPackingList(conditionQueryDto));
    }

    @Override
    public BasePacking queryByCode(BasePackingConditionQueryDto conditionQueryDto) {
        return R.getDataAndException(this.basePackingFeignService.queryByCode(conditionQueryDto));
    }
}
