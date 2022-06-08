package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpOutboundFeignFallback;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.CreateShipmentResponseVO;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 11:31
 */
@FeignClient(contextId = "FeignClient.HtpOutboundFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpOutboundFeignFallback.class)
public interface HtpOutboundFeignService {

    @PostMapping("/api/outbound/http/shipment")
    R<CreateShipmentResponseVO> shipmentCreate(@RequestBody CreateShipmentRequestDto dto);

    @DeleteMapping("/api/outbound/http/shipment")
    R<ResponseVO> shipmentDelete(@RequestBody ShipmentCancelRequestDto dto);

    @PutMapping("/api/outbound/http/shipment/tracking")
    R<ResponseVO>   shipmentTracking(@RequestBody ShipmentTrackingChangeRequestDto dto);

    @PutMapping("/api/outbound/http/shipment/label")
    R<ResponseVO> shipmentLabel(@RequestBody ShipmentLabelChangeRequestDto dto);

    @PutMapping("/api/outbound/http/shipment/shipping")
    R<ResponseVO> shipmentShipping(@RequestBody ShipmentUpdateRequestDto dto);


    @PutMapping("/api/outbound/http/shipment/multiboxrelation")
    R<ResponseVO> shipmentMultiboxrelation(@RequestBody ShipmentMultiboxrelationRequestDto dto);
}
