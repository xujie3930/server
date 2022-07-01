package com.szmsd.http.service.impl;


import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.ProblemDetails;
import com.szmsd.http.dto.ShipmentOrderResult;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenancePageRequest;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenanceDto;
import com.szmsd.http.service.IHttpChaLevelMaintenanceService;
import com.szmsd.http.service.http.SaaSPricedRequest;
import com.szmsd.http.util.HttpResponseVOUtils;
import org.apache.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HttpChaLevelMaintenanceServiceImpl extends SaaSPricedRequest implements IHttpChaLevelMaintenanceService {

    public HttpChaLevelMaintenanceServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }


    @Override
    public R<PageVO> page(ChaLevelMaintenancePageRequest pageDTO) {
        return HttpResponseVOUtils.transformationPage(
                httpPostBody("", "pricedGrade.pageResult", pageDTO), ChaLevelMaintenanceDto.class);
    }

    @Override
    public R<ChaLevelMaintenanceDto> get(String id) {
        return HttpResponseVOUtils.transformation(httpGetBody("", "pricedGrade.get", null, id), ChaLevelMaintenanceDto.class);
    }

    @Override
    public R create(ChaLevelMaintenanceDto chaLevelMaintenanceCreate) {
        return HttpResponseVOUtils.transformation(httpPostBody("", "pricedGrade.create", chaLevelMaintenanceCreate));
    }

    @Override
    public R update(ChaLevelMaintenanceDto chaLevelMaintenanceUpdate) {
        return HttpResponseVOUtils.transformation(httpPutBody("", "pricedGrade.update", chaLevelMaintenanceUpdate, chaLevelMaintenanceUpdate.getId()));
    }

    @Override
    public R delete(String id) {
        return HttpResponseVOUtils.transformation(httpDeleteBody("", "pricedGrade.delete", null, id));
    }

    @Override
    public R<List<ChaLevelMaintenanceDto>> list(ChaLevelMaintenanceDto pageDTO) {
        return HttpResponseVOUtils.transformationList(
                httpGetBody("", "pricedGrade.list", pageDTO), ChaLevelMaintenanceDto.class);
    }
}
