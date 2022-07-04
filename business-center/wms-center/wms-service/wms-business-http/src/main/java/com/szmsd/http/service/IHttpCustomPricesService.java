package com.szmsd.http.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.dto.custom.*;

public interface IHttpCustomPricesService {
        R<CustomPricesPageDto> page(String clientCode);

        R updateDiscountDetail(CustomDiscountMainDto dto);

        R updateGradeDetail(CustomGradeMainDto dto);


        R updateDiscount(UpdateCustomMainDto dto);

        R updateGrade(UpdateCustomMainDto dto);




}
