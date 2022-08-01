package com.szmsd.bas.api.service;

import com.szmsd.bas.domain.BasePacking;
import com.szmsd.bas.domain.SysLanres;
import com.szmsd.bas.dto.BasePackingConditionQueryDto;
import com.szmsd.bas.dto.BasePackingDto;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.bas.dto.CreatePackingRequest;
import com.szmsd.common.core.domain.R;

import java.util.List;

public interface BasePackingClientService {

    /**
     * 根据仓库，SKU查询产品信息
     *
     * @param conditionQueryDto conditionQueryDto
     * @return BasePacking
     */
    List<BasePacking> queryPackingList(BaseProductConditionQueryDto conditionQueryDto);

    /**
     * 根据编码查询
     *
     * @param conditionQueryDto conditionQueryDto
     * @return BasePacking
     */
    BasePacking queryByCode(BasePackingConditionQueryDto conditionQueryDto);

    R createPackings(CreatePackingRequest createPackingRequest);

    List<BasePackingDto> listParent(BasePackingDto warehouseCode);
    List<BasePackingDto> listParent();

    R selectSysLanresList(SysLanres sysLanres);
}
