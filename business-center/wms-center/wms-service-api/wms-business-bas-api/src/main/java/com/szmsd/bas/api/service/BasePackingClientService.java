package com.szmsd.bas.api.service;

import com.szmsd.bas.domain.BasePacking;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;

import java.util.List;

public interface BasePackingClientService {

    /**
     * 根据仓库，SKU查询产品信息
     *
     * @param conditionQueryDto conditionQueryDto
     * @return BasePacking
     */
    List<BasePacking> queryPackingList(BaseProductConditionQueryDto conditionQueryDto);
}
