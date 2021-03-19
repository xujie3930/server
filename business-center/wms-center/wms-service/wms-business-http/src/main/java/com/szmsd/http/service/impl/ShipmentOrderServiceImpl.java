package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.service.IShipmentOrderService;
import com.szmsd.http.vo.CarrierService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipmentOrderServiceImpl extends AbstractCarrierServiceHttpRequest implements IShipmentOrderService {

    public ShipmentOrderServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public List<CarrierService> services() {
        return JSON.parseArray(httpGet(httpConfig.getCarrierService().getServices(), null), CarrierService.class);
    }
}
