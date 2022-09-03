package com.szmsd.bas.service;

import com.szmsd.bas.domain.BasMeteringConfig;
import com.szmsd.bas.dto.BasMeteringConfigDto;
import com.szmsd.common.core.domain.R;

import java.util.List;

public interface IBasMeteringConfigService {

    //查询计泡规则主界面接口
    List<BasMeteringConfig> selectList(BasMeteringConfigDto basMeteringConfigDto);

    //新增计泡规则
    R insertBasMeteringConfig(BasMeteringConfig basMeteringConfig);
     //修改计泡规则
     R UpdateBasMeteringConfig(BasMeteringConfig basMeteringConfig);

    R selectById(Integer id);

    R intercept(BasMeteringConfigDto basMeteringConfigDto);
}
