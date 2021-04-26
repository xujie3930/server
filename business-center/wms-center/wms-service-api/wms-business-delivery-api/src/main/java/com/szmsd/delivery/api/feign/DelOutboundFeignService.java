package com.szmsd.delivery.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.api.BusinessDeliveryInterface;
import com.szmsd.delivery.api.feign.factory.DelOutboundFeignFallback;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.dto.ShipmentContainersRequestDto;
import com.szmsd.delivery.dto.ShipmentPackingMaterialRequestDto;
import com.szmsd.delivery.dto.ShipmentRequestDto;
import com.szmsd.delivery.vo.DelOutboundDetailListVO;
import com.szmsd.delivery.vo.DelOutboundDetailVO;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;
import io.swagger.annotations.ApiOperation;
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

    /**
     * 出库费用查询
     * @param queryDto queryDto
     * @return pageList
     */
    @PostMapping("/api/outbound/delOutboundCharge/page")
    R<TableDataInfo<QueryChargeVO>> getDelOutboundCharge(@RequestBody QueryChargeDto queryDto);

    /**
     * 查询采购单中的sku列表
     * @param idList
     * @return
     */
    @GetMapping(value = "createPurchaseOrderListByIdList/{idList}")
    @ApiOperation(value = "出库-创建采购单")
    R<List<DelOutboundDetailVO>> createPurchaseOrderListByIdList(@PathVariable("idList") List<String> idList);


}
