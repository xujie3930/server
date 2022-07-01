package com.szmsd.chargerules.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.dto.custom.*;

public interface ICustomPricesService {

    R updateDiscountDetail(CustomDiscountMainDto dto);

    R updateGradeDetail(CustomGradeMainDto dto);

    R<CustomPricesMainDto> result(String clientCode);

    R updateDiscount(UpdateCustomDiscountMainDto dto);

    R updateGrade(UpdateCustomGradeMainDto dto);


    R discountDetailResult(String id);

    R gradeDetailResult(String id);

}
