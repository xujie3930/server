package com.szmsd.delivery.service.wrapper.impl;

import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.service.wrapper.IDelOutboundHttpWrapperService;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.CreateShipmentResponseVO;
import com.szmsd.http.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 16:50
 */
@Service
public class DelOutboundHttpWrapperServiceImpl implements IDelOutboundHttpWrapperService {

    @Autowired
    private IHtpOutboundClientService htpOutboundClientService;

    @Override
    public String shipmentCreate(DelOutboundDto dto, String refOrderNo) {
        CreateShipmentRequestDto requestDto = BeanMapperUtil.map(dto, CreateShipmentRequestDto.class);
        // 处理refOrderNo
        requestDto.setRefOrderNo(refOrderNo);
        CreateShipmentResponseVO responseVO = htpOutboundClientService.shipmentCreate(requestDto);
        ResponseVO.assertResponse(responseVO, "调用WMS创建出库单失败");
        if (StringUtils.isEmpty(responseVO.getOrderNo())) {
            throw new CommonException("999", "调用WMS创建出库单失败，未获取到OrderNo");
        }
        return responseVO.getOrderNo();
    }

    @Override
    public Boolean shipmentDelete(ShipmentCancelRequestDto dto) {
        ResponseVO responseVO = htpOutboundClientService.shipmentDelete(dto);
        ResponseVO.assertResponse(responseVO, "调用WMS取消出库单失败");
        return responseVO.getSuccess();
    }

    @Override
    public Boolean shipmentTracking(ShipmentTrackingChangeRequestDto dto) {
        return null;
    }

    @Override
    public Boolean shipmentLabel(ShipmentLabelChangeRequestDto dto) {
        return null;
    }

    @Override
    public Boolean shipmentShipping(ShipmentUpdateRequestDto dto) {
        return null;
    }
}
