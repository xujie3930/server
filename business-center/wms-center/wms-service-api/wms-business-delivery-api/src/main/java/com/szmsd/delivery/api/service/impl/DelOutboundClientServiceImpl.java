package com.szmsd.delivery.api.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.api.service.DelOutboundClientService;
import com.szmsd.delivery.domain.DelOutboundPacking;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.vo.*;
import com.szmsd.http.vo.PricedProduct;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
    public DelOutboundAddResponse addShipment(DelOutboundDto dto) {
        return R.getDataAndException(this.delOutboundFeignService.addShipment(dto));
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

    @Override
    public List<DelOutboundPackingVO> listByOrderNo(DelOutboundPacking request) {
        return R.getDataAndException(this.delOutboundFeignService.listByOrderNo(request));
    }

    @Override
    public int againTrackingNo(DelOutboundAgainTrackingNoDto dto) {
        return R.getDataAndException(this.delOutboundFeignService.againTrackingNo(dto));
    }

    @Override
    public List<DelOutboundListExceptionMessageVO> exceptionMessageList(List<String> orderNos) {
        if (CollectionUtils.isEmpty(orderNos)) {
            return Collections.emptyList();
        }
        return R.getDataAndException(this.delOutboundFeignService.exceptionMessageList(orderNos));
    }

    @Override
    public List<DelOutboundListExceptionMessageExportVO> exceptionMessageExportList(List<String> orderNos) {
        if (CollectionUtils.isEmpty(orderNos)) {
            return Collections.emptyList();
        }
        return R.getDataAndException(this.delOutboundFeignService.exceptionMessageExportList(orderNos));
    }

    @Override
    public List<DelOutboundBringVerifyVO> bringVerify(DelOutboundBringVerifyDto dto) {
        return R.getDataAndException(this.delOutboundFeignService.bringVerify(dto));
    }

    @Override
    public void updateShipmentLabel(List<String> idList) {
        R.getDataAndException(this.delOutboundFeignService.updateShipmentLabel(idList));
    }
}
