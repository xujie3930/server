package com.szmsd.delivery.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.api.BusinessDeliveryInterface;
import com.szmsd.delivery.api.feign.factory.DelOutboundReportFeignFallback;
import com.szmsd.delivery.dto.DelOutboundReportQueryDto;
import com.szmsd.delivery.vo.DelOutboundReportListVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 14:32
 */
@FeignClient(contextId = "FeignClient.DelOutboundReportFeignService", name = BusinessDeliveryInterface.SERVICE_NAME, fallbackFactory = DelOutboundReportFeignFallback.class)
public interface DelOutboundReportFeignService {

    @PostMapping("/api/outbound/report/create")
    R<List<DelOutboundReportListVO>> queryCreateData(@RequestBody DelOutboundReportQueryDto queryDto);

    @PostMapping("/api/outbound/report/bringVerify")
    R<List<DelOutboundReportListVO>> queryBringVerifyData(@RequestBody DelOutboundReportQueryDto queryDto);

    @PostMapping("/api/outbound/report/outbound")
    R<List<DelOutboundReportListVO>> queryOutboundData(@RequestBody DelOutboundReportQueryDto queryDto);
}
