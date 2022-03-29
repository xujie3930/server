package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.CreatePickupPackageCommand;
import com.szmsd.http.dto.ProblemDetails;
import com.szmsd.http.dto.ResponseObject;
import com.szmsd.http.dto.ShipmentOrderResult;
import com.szmsd.http.service.IPickupPackageService;
import com.szmsd.http.service.http.SaaSCarrierServiceAdminRequest;
import com.szmsd.http.vo.PickupPackageService;
import com.szmsd.http.vo.PricedSheet;
import com.szmsd.http.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : wangshuai
 * @date : 2022-03-25 21:49
 * @description :
 **/
@Slf4j
@Service
public class PickupPackageServiceImpl extends SaaSCarrierServiceAdminRequest implements IPickupPackageService {
    public PickupPackageServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public List<PickupPackageService> services() {
        return JSON.parseObject(httpGet("", "shipment-order.pickup-package-services", null), List.class);
    }

    @Override
    public ResponseVO create(CreatePickupPackageCommand command) {
        return JSON.parseObject(httpPost("", "shipment-order.pickup-package-create", command), ResponseVO.class);
    }
}
