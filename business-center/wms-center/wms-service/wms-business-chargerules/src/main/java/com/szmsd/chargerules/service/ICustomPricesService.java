package com.szmsd.chargerules.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.dto.OperationRecordDto;
import com.szmsd.http.dto.custom.*;

import java.util.List;

public interface ICustomPricesService {
    R<OperationRecordDto> operationRecord(String id);
    R updateDiscountDetail(CustomDiscountMainDto dto);

    R updateGradeDetail(CustomGradeMainDto dto);

    List<BasCustomPricesgradeDto> result(BasCustomPricesgradeDto basCustomPricesgradeDto);

    R updateDiscount(UpdateCustomMainDto dto);

    R updateGrade(UpdateCustomMainDto dto);


    R discountDetailResult(String id);

    R gradeDetailResult(String id);

}
