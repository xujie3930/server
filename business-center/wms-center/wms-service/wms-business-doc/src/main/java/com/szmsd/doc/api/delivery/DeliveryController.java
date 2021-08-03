package com.szmsd.doc.api.delivery;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.delivery.api.service.DelOutboundClientService;
import com.szmsd.delivery.dto.DelOutboundCanceledDto;
import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.dto.DelOutboundOtherInServiceDto;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
import com.szmsd.doc.api.delivery.request.DelOutboundCanceledRequest;
import com.szmsd.doc.api.delivery.request.DelOutboundPackageTransferListRequest;
import com.szmsd.doc.api.delivery.request.DelOutboundPackageTransferRequest;
import com.szmsd.doc.api.delivery.request.PricedProductRequest;
import com.szmsd.doc.api.delivery.request.group.DelOutboundGroup;
import com.szmsd.doc.api.delivery.response.DelOutboundPackageTransferResponse;
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
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "List", required = true)
    public R<List<DelOutboundPackageTransferResponse>> packageTransferLabel(@RequestBody @Validated(value = {DelOutboundGroup.PackageTransfer.class}) List<DelOutboundPackageTransferRequest> request) {
        if (CollectionUtils.isEmpty(request)) {
            throw new CommonException("999", "请求对象不能为空");
        }
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(request, DelOutboundDto.class);
        List<DelOutboundAddResponse> responseList = delOutboundClientService.add(dtoList);
        return R.ok(BeanMapperUtil.mapList(responseList, DelOutboundPackageTransferResponse.class));
    }

    @PreAuthorize("hasAuthority('read')")
    @DeleteMapping("/package-transfer")
    @ApiOperation(value = "#4 出库管理 - 取消单据（转运出库）", position = 202)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundPackageTransferRequest", required = true)
    public R<List<DelOutboundPackageTransferResponse>> packageTransferCancel(@RequestBody @Validated(value = {DelOutboundGroup.PackageTransfer.class}) List<DelOutboundPackageTransferRequest> request) {
        if (CollectionUtils.isEmpty(request)) {
            throw new CommonException("999", "请求对象不能为空");
        }
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(request, DelOutboundDto.class);
        List<DelOutboundAddResponse> responseList = delOutboundClientService.add(dtoList);
        return R.ok(BeanMapperUtil.mapList(responseList, DelOutboundPackageTransferResponse.class));
    }

    @PreAuthorize("hasAuthority('read')")
    @PostMapping("/canceled")
    @ApiOperation(value = "出库管理 - 取消", position = 700)
    @ApiImplicitParam(name = "request", value = "请求参数", required = true, dataType = "DelOutboundCanceledRequest")
    public R<Integer> canceled(@RequestBody @Validated List<DelOutboundCanceledRequest> request) {
        DelOutboundCanceledDto dto = BeanMapperUtil.map(request, DelOutboundCanceledDto.class);
        return R.ok(this.delOutboundClientService.canceled(dto));
    }
}
