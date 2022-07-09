package com.szmsd.http.service.impl;


import com.szmsd.common.core.domain.R;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.OperationRecordDto;
import com.szmsd.http.dto.custom.*;
import com.szmsd.http.dto.grade.GradeMainDto;
import com.szmsd.http.service.IHttpCustomPricesService;
import com.szmsd.http.service.http.SaaSPricedRequest;
import com.szmsd.http.util.HttpResponseVOUtils;
import org.springframework.stereotype.Service;

@Service
public class HttpCustomPricesServiceImpl extends SaaSPricedRequest implements IHttpCustomPricesService {

    public HttpCustomPricesServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }


    @Override
    public R updateDiscountDetail(CustomDiscountMainDto dto) {
            return HttpResponseVOUtils.transformation(httpPutBody("", "customPrices.updateDetailDiscount", dto, dto.getClientCode()));
    }

    @Override
    public R updateGradeDetail(CustomGradeMainDto dto) {
        return HttpResponseVOUtils.transformation(httpPutBody("", "customPrices.updateDetailGrade", dto, dto.getClientCode()));
    }

    @Override
    public R<CustomPricesPageDto> page(String clientCode) {
        return HttpResponseVOUtils.transformation(httpGetBody("", "customPrices.page", null, clientCode), CustomPricesPageDto.class);
    }

    @Override
    public R updateDiscount(UpdateCustomMainDto dto) {
        return HttpResponseVOUtils.transformation(httpPutBody("", "customPrices.updateDiscount", dto, dto.getClientCode()));
    }

    @Override
    public R updateGrade(UpdateCustomMainDto dto) {
        return HttpResponseVOUtils.transformation(httpPutBody("", "customPrices.updateGrade", dto, dto.getClientCode()));
    }

    @Override
    public R<OperationRecordDto> operationRecord(String id) {
        return HttpResponseVOUtils.transformation(httpGetBody("", "customPrices.operationRecord", null, id), OperationRecordDto.class);

    }

}
