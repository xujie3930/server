package com.szmsd.doc.api.delivery;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.api.service.DelOutboundClientService;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
import com.szmsd.delivery.vo.DelOutboundLabelResponse;
import com.szmsd.delivery.vo.DelOutboundListVO;
import com.szmsd.doc.api.delivery.request.*;
import com.szmsd.doc.api.delivery.request.group.DelOutboundGroup;
import com.szmsd.doc.api.delivery.response.DelOutboundPackageTransferResponse;
import com.szmsd.doc.api.delivery.response.DelOutboundShipmentResponse;
import com.szmsd.doc.api.delivery.response.PricedProductResponse;
import com.szmsd.http.vo.PricedProduct;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-07-28 16:05
 */
@Api(tags = {"出库管理"})
@ApiSort(100)
@RestController
@RequestMapping("/api/outbound")
public class DeliveryController {

    @Autowired
    private DelOutboundClientService delOutboundClientService;
    @Autowired
    private DelOutboundFeignService delOutboundFeignService;

    @PreAuthorize("hasAuthority('read')")
    @PostMapping("/priced-product")
    @ApiOperation(value = "#1 出库管理 - 物流服务列表", position = 100)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "PricedProductRequest", required = true)
    public R<List<PricedProductResponse>> pricedProduct(@RequestBody @Validated PricedProductRequest request) {
        if (CollectionUtils.isEmpty(request.getSkus()) && CollectionUtils.isEmpty(request.getProductAttributes())) {
            throw new CommonException("500", "SKU，产品属性信息不能全部为空");
        }
        DelOutboundOtherInServiceDto dto = BeanMapperUtil.map(request, DelOutboundOtherInServiceDto.class);
        List<PricedProduct> productList = this.delOutboundClientService.inService(dto);
        if (CollectionUtils.isEmpty(productList)) {
            return R.ok();
        }
        return R.ok(BeanMapperUtil.mapList(productList, PricedProductResponse.class));
    }

    @PreAuthorize("hasAuthority('read')")
    @PostMapping("/package-transfer")
    @ApiOperation(value = "#2 出库管理 - 单据创建（转运出库）", position = 200)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundPackageTransferListRequest", required = true)
    public R<List<DelOutboundPackageTransferResponse>> packageTransfer(@RequestBody @Validated(value = {DelOutboundGroup.PackageTransfer.class}) DelOutboundPackageTransferListRequest request) {
        List<DelOutboundPackageTransferRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("999", "请求对象不能为空");
        }
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(requestList, DelOutboundDto.class);
        List<DelOutboundAddResponse> responseList = delOutboundClientService.add(dtoList);
        return R.ok(BeanMapperUtil.mapList(responseList, DelOutboundPackageTransferResponse.class));
    }

    @PreAuthorize("hasAuthority('read')")
    @GetMapping("/package-transfer/label")
    @ApiOperation(value = "#3 出库管理 - 获取标签（转运出库）", position = 201)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundLabelRequest", required = true)
    public R<List<DelOutboundLabelResponse>> packageTransferLabel(@RequestBody @Validated DelOutboundLabelRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("999", "订单号不能为空");
        }
        DelOutboundLabelDto labelDto = new DelOutboundLabelDto();
        labelDto.setOrderNos(orderNos);
        return R.ok(this.delOutboundClientService.labelBase64(labelDto));
    }

    @PreAuthorize("hasAuthority('read')")
    @DeleteMapping("/cancel/package-transfer")
    @ApiOperation(value = "#4 出库管理 - 取消单据（转运出库）", position = 202)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundCanceledRequest", required = true)
    public R<Integer> cancelPackageTransfer(@RequestBody @Validated DelOutboundCanceledRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("999", "订单号不能为空");
        }
        DelOutboundCanceledDto canceledDto = new DelOutboundCanceledDto();
        canceledDto.setOrderNos(orderNos);
        return R.ok(this.delOutboundClientService.canceled(canceledDto));
    }

    @PreAuthorize("hasAuthority('read')")
    @PostMapping("/page")
    @ApiOperation(value = "#5 出库管理 - 查询订单列表", position = 300)
    @ApiImplicitParam(name = "dto", value = "请求参数", dataType = "DelOutboundListQueryDto", required = true)
    public TableDataInfo<DelOutboundListVO> page(@RequestBody DelOutboundListQueryDto dto) {
        return this.delOutboundFeignService.page(dto);
    }

    @PreAuthorize("hasAuthority('read')")
    @PostMapping("/shipment")
    @ApiOperation(value = "#6 出库管理 - 订单创建（一件代发）", position = 400)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundShipmentListRequest", required = true)
    public R<List<DelOutboundShipmentResponse>> shipment(@RequestBody @Validated DelOutboundShipmentListRequest request) {
        List<DelOutboundShipmentRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("999", "请求对象不能为空");
        }
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(requestList, DelOutboundDto.class);
        List<DelOutboundAddResponse> responseList = delOutboundClientService.add(dtoList);
        return R.ok(BeanMapperUtil.mapList(responseList, DelOutboundShipmentResponse.class));
    }

    @PreAuthorize("hasAuthority('read')")
    @DeleteMapping("/cancel/shipment")
    @ApiOperation(value = "#7 出库管理 - 取消单据（一件代发）", position = 401)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundCanceledRequest", required = true)
    public R<Integer> cancelShipment(@RequestBody @Validated DelOutboundCanceledRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("999", "订单号不能为空");
        }
        DelOutboundCanceledDto canceledDto = new DelOutboundCanceledDto();
        canceledDto.setOrderNos(orderNos);
        return R.ok(this.delOutboundClientService.canceled(canceledDto));
    }

    // @ApiOperation(value = "#8 出库管理 - 订单创建（集运出库）", position = 500)

    // @ApiOperation(value = "#9 出库管理 - 取消单据（集运出库）", position = 501)

    // @ApiOperation(value = "#10 出库管理 - 更新信息（集运出库）", position = 502)

    // @ApiOperation(value = "#11 出库管理 - 更新信息（集运出库）", position = 502)
}
