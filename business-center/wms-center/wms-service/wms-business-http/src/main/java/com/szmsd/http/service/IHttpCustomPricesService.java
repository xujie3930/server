package com.szmsd.http.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.dto.custom.*;

public interface IHttpCustomPricesService {

        R updateDiscountDetail(CustomDiscountMainDto dto);

        R updateGradeDetail(CustomGradeMainDto dto);

        R<CustomPricesMainDto> result(String clientCode);

        R updateDiscount(UpdateCustomDiscountMainDto dto);

        R updateGrade(UpdateCustomGradeMainDto dto);




}
