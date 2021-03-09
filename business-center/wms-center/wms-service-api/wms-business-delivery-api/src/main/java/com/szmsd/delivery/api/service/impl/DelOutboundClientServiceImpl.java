package com.szmsd.delivery.api.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.api.service.DelOutboundClientService;
import com.szmsd.delivery.dto.PackageMeasureRequestDto;
import com.szmsd.delivery.dto.ShipmentContainersRequestDto;
import com.szmsd.delivery.dto.ShipmentPackingMaterialRequestDto;
import com.szmsd.delivery.dto.ShipmentRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 14:36
 */
@Service
public class DelOutboundClientServiceImpl implements DelOutboundClientService {

    @Autowired
    private DelOutboundFeignService delOutboundFeignService;

    @Override
    public int shipment(ShipmentRequestDto dto) {
        return R.getDataAndException(this.delOutboundFeignService.shipment(dto));
    }

    @Override
    public int shipmentMeasure(PackageMeasureRequestDto dto) {
        return R.getDataAndException(this.delOutboundFeignService.shipmentMeasure(dto));
    }

    @Override
    public int shipmentPacking(ShipmentPackingMaterialRequestDto dto) {
        return R.getDataAndException(this.delOutboundFeignService.shipmentPacking(dto));
    }

    @Override
    public int shipmentContainers(ShipmentContainersRequestDto dto) {
        return R.getDataAndException(this.delOutboundFeignService.shipmentContainers(dto));
    }
}
