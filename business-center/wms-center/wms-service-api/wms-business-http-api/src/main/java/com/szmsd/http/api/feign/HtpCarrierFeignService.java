package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpCarrierFeignFallback;
import com.szmsd.http.dto.CreateShipmentOrderCommand;
import com.szmsd.http.dto.ProblemDetails;
import com.szmsd.http.dto.ResponseObject;
import com.szmsd.http.dto.ShipmentOrderResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 11:50
 */
@FeignClient(contextId = "FeignClient.HtpCarrierFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpCarrierFeignFallback.class)
public interface HtpCarrierFeignService {

    @PostMapping("/api/carrier/http/shipmentOrder")
    R<ResponseObject.ResponseObjectWrapper<ShipmentOrderResult, ProblemDetails>> shipmentOrder(@RequestBody CreateShipmentOrderCommand command);

    @GetMapping("/api/carrier/http/label")
    R<ResponseObject.ResponseObjectWrapper<FileStream, ProblemDetails>> label(@RequestParam("orderNumber") String orderNumber);
}
