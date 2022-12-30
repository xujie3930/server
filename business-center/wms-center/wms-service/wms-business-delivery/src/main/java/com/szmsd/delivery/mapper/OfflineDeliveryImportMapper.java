package com.szmsd.delivery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.delivery.domain.OfflineDeliveryImport;
import com.szmsd.delivery.dto.OfflineImportDto;

import java.util.List;

public interface OfflineDeliveryImportMapper extends BaseMapper<OfflineDeliveryImport> {
    int saveBatch(List<OfflineDeliveryImport> offlineDeliveryImports);

    int updateDealState(List<OfflineImportDto> updateData);

    //int updateDealStateByOrder(List<OfflineImportDto> updateData);
}
