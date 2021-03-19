package com.szmsd.chargerules.service.impl;

import com.szmsd.chargerules.service.IShipmentOrderService;
import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpShipmentOrderFeignService;
import com.szmsd.http.vo.CarrierService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ShipmentOrderServiceImpl implements IShipmentOrderService {

    @Resource
    private HtpShipmentOrderFeignService htpShipmentOrderFeignService;

    /**
     * 挂号服务
     * https://carrierservice-api-admin-external.dsloco.com/api/v1/carrierService/shipmentOrders/services
     * @return
     */
    @Override
    public List<CarrierService> services() {
        R<List<CarrierService>> services = htpShipmentOrderFeignService.services();
        return R.getDataAndException(services);
    }
}
