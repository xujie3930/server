package com.szmsd.delivery.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.api.BusinessDeliveryInterface;
import com.szmsd.delivery.api.feign.factory.DelOutboundFeignFallback;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.dto.ShipmentContainersRequestDto;
import com.szmsd.delivery.dto.ShipmentPackingMaterialRequestDto;
import com.szmsd.delivery.dto.ShipmentRequestDto;
import com.szmsd.delivery.vo.DelOutboundDetailListVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

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

    /**
     * 根据单号查询出库单详情
     *
     * @param orderId orderId
     * @return DelOutbound
     */
    @GetMapping("/api/outbound/getInfoByOrderId/{orderId}")
    R<DelOutbound> details(@PathVariable("orderId") String orderId);

    /**
     * 根据单号查询出库单详情列表
     *
     * @param queryDto queryDto
     * @return List<DelOutboundDetailListVO>
     */
    @PostMapping("/api/outbound/getDelOutboundDetailsList")
    R<List<DelOutboundDetailListVO>> getDelOutboundDetailsList(@RequestBody DelOutboundListQueryDto queryDto);
}
