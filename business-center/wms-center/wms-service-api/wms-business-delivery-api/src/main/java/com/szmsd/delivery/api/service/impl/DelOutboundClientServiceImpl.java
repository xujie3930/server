package com.szmsd.delivery.api.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.api.service.DelOutboundClientService;
import com.szmsd.delivery.domain.DelOutboundPacking;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
import com.szmsd.delivery.vo.DelOutboundLabelResponse;
import com.szmsd.http.vo.PricedProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public int shipmentPacking(ShipmentPackingMaterialRequestDto dto) {
        return R.getDataAndException(this.delOutboundFeignService.shipmentPacking(dto));
    }

    @Override
    public int shipmentContainers(ShipmentContainersRequestDto dto) {
        return R.getDataAndException(this.delOutboundFeignService.shipmentContainers(dto));
    }

    @Override
    public int furtherHandler(DelOutboundFurtherHandlerDto dto) {
        return R.getDataAndException(this.delOutboundFeignService.furtherHandler(dto));
    }

    @Override
    public int canceled(DelOutboundCanceledDto dto) {
        return R.getDataAndException(this.delOutboundFeignService.canceled(dto));
    }

    @Override
    public List<PricedProduct> inService(DelOutboundOtherInServiceDto dto) {
        return R.getDataAndException(this.delOutboundFeignService.inService(dto));
    }

    @Override
    public List<DelOutboundAddResponse> add(List<DelOutboundDto> dto) {
        return R.getDataAndException(this.delOutboundFeignService.add(dto));
    }

    @Override
    public List<DelOutboundLabelResponse> labelBase64(DelOutboundLabelDto dto) {
        return R.getDataAndException(this.delOutboundFeignService.labelBase64(dto));
    }

    @Override
    public int uploadBoxLabel(DelOutboundUploadBoxLabelDto dto) {
        return R.getDataAndException(this.delOutboundFeignService.uploadBoxLabel(dto));
    }

    @Override
    public List<DelOutboundPacking> queryList(DelOutboundPacking request) {
        return R.getDataAndException(this.delOutboundFeignService.queryList(request));
    }
}
