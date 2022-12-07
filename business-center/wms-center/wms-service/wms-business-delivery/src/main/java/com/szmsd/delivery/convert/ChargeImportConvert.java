package com.szmsd.delivery.convert;

import com.szmsd.delivery.domain.ChargeImport;
import com.szmsd.delivery.dto.ChargeExcelDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ChargeImportConvert {

    ChargeImportConvert INSTANCE = Mappers.getMapper(ChargeImportConvert.class);

    ChargeImport toChargeImport(ChargeExcelDto chargeExcelDto);

    List<ChargeImport> toChargeImportList(List<ChargeExcelDto> chargeExcelDtos);

}
