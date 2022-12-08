package com.szmsd.delivery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.delivery.domain.ChargeImport;
import com.szmsd.delivery.dto.ChargePricingOrderMsgDto;

import java.util.List;

public interface ChargeImportMapper extends BaseMapper<ChargeImport> {

    int batchUpd(List<ChargePricingOrderMsgDto> allData);
}
