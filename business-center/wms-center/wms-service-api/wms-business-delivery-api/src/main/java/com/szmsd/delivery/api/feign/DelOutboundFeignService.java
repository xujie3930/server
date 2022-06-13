package com.szmsd.delivery.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.api.BusinessDeliveryInterface;
import com.szmsd.delivery.api.feign.factory.DelOutboundFeignFallback;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundPacking;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.vo.*;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;
import com.szmsd.http.vo.PricedProduct;
import com.szmsd.inventory.domain.dto.QueryFinishListDTO;
import com.szmsd.inventory.domain.vo.QueryFinishListVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
     * 出库管理 - Open - 接收出库包裹使用包材(拆分为D21和D22)
     *
     * @param dto dto
     * @return Integer
     */
    @PostMapping("/api/outbound/open/shipment/packing")
    R<Integer> shipmentPacking(@RequestBody ShipmentPackingMaterialRequestDto dto);

    /**
     * 出库管理 - Open - 接收出库包裹包材信息
     *
     * @param dto dto
     * @return Integer
     */
    @PostMapping("/api/outbound/open/shipment/packing/material")
    R<Integer> shipmentPackingMaterial(@RequestBody ShipmentPackingMaterialRequestDto dto);

    /**
     * 出库管理 - Open - 接收出库包裹测量信息
     *
     * @param dto dto
     * @return Integer
     */
    @PostMapping("/api/outbound/open/shipment/packing/measure")
    R<Integer> shipmentPackingMeasure(@RequestBody ShipmentPackingMaterialRequestDto dto);

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

    @GetMapping(value = "/api/outbound/getInfoForThirdParty/{orderNo}")
    R<DelOutboundThirdPartyVO> getInfoForThirdParty(@PathVariable("orderNo") String orderNo);
    /**
     * 出库费用查询
     *
     * @param queryDto queryDto
     * @return pageList
     */
    @PostMapping("/api/outbound/delOutboundCharge/page")
    R<TableDataInfo<QueryChargeVO>> getDelOutboundCharge(@RequestBody QueryChargeDto queryDto);

    /**
     * 查询采购单中的sku列表
     *
     * @param idList
     * @return
     */
    @GetMapping(value = "/api/outbound/createPurchaseOrderListByIdList/{idList}")
    @ApiOperation(value = "出库-创建采购单")
    R<List<DelOutboundDetailVO>> createPurchaseOrderListByIdList(@PathVariable("idList") List<String> idList);

    @GetMapping(value = "/api/outbound/getTransshipmentProductData/{idList}")
    @ApiOperation(value = "转运-获取转运里面的商品数据")
    R<List<DelOutboundDetailVO>> getTransshipmentProductData(@PathVariable("idList") List<String> idList);

    /**
     * 出库-创建采购单后回写出库单 采购单号
     * 多个出库单，对应一个采购单
     *
     * @param purchaseNo  采购单号
     * @param orderNoList 出库单列表
     * @return
     */
    @GetMapping(value = "/api/outbound/purchase/setPurchaseNo/{purchaseNo}/{orderNoList}")
    @ApiOperation(value = "出库-实际创建采购单后回写采购单号")
    R setPurchaseNo(@PathVariable("purchaseNo") String purchaseNo, @PathVariable("orderNoList") List<String> orderNoList);

    /**
     * 继续处理
     *
     * @param dto dto
     * @return R<Integer>
     */
    @PostMapping("/api/outbound/furtherHandler")
    R<Integer> furtherHandler(@RequestBody @Validated DelOutboundFurtherHandlerDto dto);

    @PostMapping("/api/outbound/page")
    @ApiOperation(value = "出库管理 - 分页")
    TableDataInfo<DelOutboundListVO> page(@RequestBody DelOutboundListQueryDto queryDto);

    @PostMapping("/api/outbound/canceled")
    @ApiOperation(value = "出库管理 - 取消")
    R<Integer> canceled(@RequestBody DelOutboundCanceledDto dto);

    @PostMapping("/api/outbound/other/inService")
    @ApiOperation(value = "出库管理 - 其它服务 - 物流服务")
    R<List<PricedProduct>> inService(@RequestBody DelOutboundOtherInServiceDto dto);

    @PostMapping("/api/outbound/doc/shipment")
    @ApiOperation(value = "出库管理 - DOC支持 - 创建")
    R<List<DelOutboundAddResponse>> add(@RequestBody List<DelOutboundDto> dto);

    @PostMapping("/api/outbound/shipment")
    @ApiOperation(value = "出库管理 - 创建")
    R<DelOutboundAddResponse> addShipment(@RequestBody DelOutboundDto dto);

    @PostMapping("/api/outbound/shipment-package-collection")
    @ApiOperation(value = "出库管理 - 创建揽收销毁出库单")
    R<DelOutboundAddResponse> addShipmentPackageCollection(@RequestBody DelOutboundDto dto);

    @PostMapping("/api/outbound/labelBase64")
    @ApiOperation(value = "出库管理 - 获取标签（根据订单号批量查询，DOC支持）")
    R<List<DelOutboundLabelResponse>> labelBase64(@RequestBody DelOutboundLabelDto dto);

    @PostMapping("/api/outbound/uploadBoxLabel")
    @ApiOperation(value = "出库管理 - 上传箱标")
    R<Integer> uploadBoxLabel(@RequestBody DelOutboundUploadBoxLabelDto dto);

    @PostMapping("/del-outbound-packing/queryList")
    @ApiOperation(value = "出库管理 - 装箱信息")
    R<List<DelOutboundPacking>> queryList(@RequestBody DelOutboundPacking request);

    @PostMapping("/del-outbound-packing/listByOrderNo")
    @ApiOperation(value = "出库管理 - 查询装箱信息")
    R<List<DelOutboundPackingVO>> listByOrderNo(@RequestBody DelOutboundPacking delOutboundPacking);

    @PostMapping("/api/outbound/againTrackingNo")
    @ApiOperation(value = "出库管理 - 异常列表 - 重新获取挂号")
    R<Integer> againTrackingNo(@RequestBody DelOutboundAgainTrackingNoDto dto);

    @PostMapping("/api/outbound/exceptionMessageList")
    @ApiOperation(value = "出库管理 - 异常列表 - 获取异常描述")
    R<List<DelOutboundListExceptionMessageVO>> exceptionMessageList(@RequestBody List<String> orderNos);

    @PostMapping("/api/outbound/exceptionMessageExportList")
    @ApiOperation(value = "出库管理 - 异常列表 - 获取异常描述(导出)")
    R<List<DelOutboundListExceptionMessageExportVO>> exceptionMessageExportList(@RequestBody List<String> orderNos);

    @PostMapping("/api/outbound/bringVerify")
    @ApiOperation(value = "出库管理 - 提审", position = 600)
    R<List<DelOutboundBringVerifyVO>> bringVerify(@RequestBody @Validated DelOutboundBringVerifyDto dto);

    @PostMapping("/api/outbound/doc/updateShipmentLabel")
    @ApiOperation(value = "出库管理 - DOC支持 - 修改发货指令", position = 100)
    @ApiImplicitParam(name = "idList", value = "出库单id", dataType = "String")
    R updateShipmentLabel(@RequestBody List<String> idList);

    @GetMapping(value = "/api/outbound/getInfoByOrderNo/{orderNo}")
    @ApiOperation(value = "出库管理 - 详情", position = 201)
    R<DelOutboundVO> getInfoByOrderNo(@PathVariable("orderNo") String orderNo);

    @GetMapping(value = "/api/outbound/getStatusByOrderNo")
    @ApiOperation(value = "出库管理 - 根据订单号查询订单信息", position = 202)
    R<DelOutboundVO> getStatusByOrderNo(@RequestParam("orderNo") String orderNo);

    @PostMapping("/api/outbound/queryFinishList")
    @ApiOperation(value = "查询已完成的单号", notes = "查询已完成的单号")
    TableDataInfo<QueryFinishListVO> queryFinishList(QueryFinishListDTO queryFinishListDTO);

    @PostMapping("/api/outbound/reassign")
    @ApiOperation(value = "出库管理 - 重派", position = 2200)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundDto")
    R<DelOutboundAddResponse> reassign(@RequestBody DelOutboundDto dto);

    @PostMapping("/api/outbound/addShopify")
    @ApiOperation(value = "出库管理 - 创建Shopify出库单", position = 2300)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundDto")
    R<DelOutboundAddResponse> addShopify(@RequestBody DelOutboundDto dto);
}
