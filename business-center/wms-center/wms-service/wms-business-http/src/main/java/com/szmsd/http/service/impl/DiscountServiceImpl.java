package com.szmsd.http.service.impl;


import com.szmsd.common.core.domain.R;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.custom.DiscountMainDto;
import com.szmsd.http.service.IHttpDiscountService;
import com.szmsd.http.service.http.SaaSPricedRequest;
import com.szmsd.http.util.HttpResponseVOUtils;
import org.springframework.stereotype.Service;

@Service
public class DiscountServiceImpl extends SaaSPricedRequest implements IHttpDiscountService {

    public DiscountServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }


    @Override
    public R<DiscountMainDto> detailResult(String id) {
            return HttpResponseVOUtils.transformation(httpGetBody("", "discount.detailResult", null, id), DiscountMainDto.class);
    }
}
