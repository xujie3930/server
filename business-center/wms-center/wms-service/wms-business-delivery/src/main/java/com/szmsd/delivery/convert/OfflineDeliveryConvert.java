package com.szmsd.delivery.convert;

import com.szmsd.delivery.domain.OfflineCostImport;
import com.szmsd.delivery.domain.OfflineDeliveryImport;
import com.szmsd.delivery.dto.OfflineCostExcelDto;
import com.szmsd.delivery.dto.OfflineDeliveryExcelDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OfflineDeliveryConvert {

    OfflineDeliveryConvert INSTANCE = Mappers.getMapper(OfflineDeliveryConvert.class);

    OfflineDeliveryImport toDeliveryImport(OfflineDeliveryExcelDto offlineDeliveryExcelDto);

    List<OfflineDeliveryImport> toOfflineDeliveryImportList(List<OfflineDeliveryExcelDto> offlineDeliveryExcelDtoList);

    OfflineCostImport toCostImport(OfflineCostExcelDto offlineCostExcelDto);

    List<OfflineCostImport> toOfflineCostImportList(List<OfflineCostExcelDto> costExcelDtos);
}
