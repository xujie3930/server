package com.szmsd.delivery.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.api.BusinessDeliveryInterface;
import com.szmsd.delivery.api.feign.factory.DelOutboundFeignFallback;
import com.szmsd.delivery.dto.PackageMeasureRequestDto;
import com.szmsd.delivery.dto.ShipmentContainersRequestDto;
import com.szmsd.delivery.dto.ShipmentPackingMaterialRequestDto;
import com.szmsd.delivery.dto.ShipmentRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 14:32
 */
@FeignClient(contextId = "FeignClient.DelOutboundFeignService", name = BusinessDeliveryInterface.SERVICE_NAME, fallbackFactory = DelOutboundFeignFallback.class)
public interface DelOutboundFeignService {

    /**
     * 出库管理 - Open - 接收出库单状态
     *
     * @param dto dto
     * @return Integer
     */
    @PostMapping("/api/outbound/open/shipment")
    R<Integer> shipment(@RequestBody ShipmentRequestDto dto);

    /**
     * 出库管理 - Open - 接收出库包裹测量信息
     *
     * @param dto dto
     * @return Integer
     */
    @PostMapping("/api/outbound/open/shipment/measure")
    R<Integer> shipmentMeasure(@RequestBody PackageMeasureRequestDto dto);

    /**
     * 出库管理 - Open - 接收出库包裹使用包材
     *
     * @param dto dto
     * @return Integer
     */
    @PostMapping("/api/outbound/open/shipment/packing")
    R<Integer> shipmentPacking(@RequestBody ShipmentPackingMaterialRequestDto dto);

    /**
     * 出库管理 - Open - 接收批量出库单类型装箱信息
     *
     * @param dto dto
     * @return Integer
     */
    @PostMapping("/api/outbound/open/shipment/containers")
    R<Integer> shipmentContainers(@RequestBody ShipmentContainersRequestDto dto);
}
