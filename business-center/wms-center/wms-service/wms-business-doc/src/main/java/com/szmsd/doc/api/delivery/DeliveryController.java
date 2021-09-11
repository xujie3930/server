package com.szmsd.doc.api.delivery;

import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentDataDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.api.service.DelOutboundClientService;
import com.szmsd.delivery.domain.DelOutboundPacking;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
import com.szmsd.delivery.vo.DelOutboundLabelResponse;
import com.szmsd.delivery.vo.DelOutboundListVO;
import com.szmsd.doc.api.delivery.request.*;
import com.szmsd.doc.api.delivery.request.group.DelOutboundGroup;
import com.szmsd.doc.api.delivery.response.*;
import com.szmsd.http.vo.PricedProduct;
import io.swagger.annotations.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
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
    @Autowired
    private RemoteAttachmentService remoteAttachmentService;

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/priced-product")
    @ApiOperation(value = "#1 出库管理 - 物流服务列表", position = 100, notes = "接口描述")
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

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/package-transfer")
    @ApiOperation(value = "#2 出库管理 - 单据创建（转运出库）", position = 200)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundPackageTransferListRequest", required = true)
    public R<List<DelOutboundPackageTransferResponse>> packageTransfer(@RequestBody @Validated(value = {DelOutboundGroup.PackageTransfer.class}) DelOutboundPackageTransferListRequest request) {
        List<DelOutboundPackageTransferRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("999", "请求对象不能为空");
        }
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(requestList, DelOutboundDto.class);
        for (DelOutboundDto dto : dtoList) {
            dto.setOrderType(DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode());
            dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_DOC);
        }
        List<DelOutboundAddResponse> responseList = delOutboundClientService.add(dtoList);
        return R.ok(BeanMapperUtil.mapList(responseList, DelOutboundPackageTransferResponse.class));
    }

    @PreAuthorize("hasAuthority('client')")
    @GetMapping("/package-transfer/label")
    @ApiOperation(value = "#3 出库管理 - 获取标签（转运出库）", position = 201, notes = "")
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

    @PreAuthorize("hasAuthority('client')")
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
        canceledDto.setOrderType(DelOutboundOrderTypeEnum.PACKAGE_TRANSFER);
        return R.ok(this.delOutboundClientService.canceled(canceledDto));
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/page")
    @ApiOperation(value = "#5 出库管理 - 查询订单列表", position = 300)
    @ApiImplicitParam(name = "dto", value = "请求参数", dataType = "DelOutboundListQueryDto", required = true)
    public TableDataInfo<DelOutboundListVO> page(@RequestBody DelOutboundListQueryDto dto) {
        return this.delOutboundFeignService.page(dto);
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/shipment")
    @ApiOperation(value = "#6 出库管理 - 订单创建（一件代发）", position = 400)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundShipmentListRequest", required = true)
    public R<List<DelOutboundShipmentResponse>> shipment(@RequestBody @Validated(DelOutboundGroup.Normal.class) DelOutboundShipmentListRequest request) {
        List<DelOutboundShipmentRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("999", "请求对象不能为空");
        }
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(requestList, DelOutboundDto.class);
        for (DelOutboundDto dto : dtoList) {
            dto.setOrderType(DelOutboundOrderTypeEnum.NORMAL.getCode());
            dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_DOC);
        }
        List<DelOutboundAddResponse> responseList = delOutboundClientService.add(dtoList);
        return R.ok(BeanMapperUtil.mapList(responseList, DelOutboundShipmentResponse.class));
    }

    @PreAuthorize("hasAuthority('client')")
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
        canceledDto.setOrderType(DelOutboundOrderTypeEnum.NORMAL);
        return R.ok(this.delOutboundClientService.canceled(canceledDto));
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/collection")
    @ApiOperation(value = "#8 出库管理 - 订单创建（集运出库）", position = 500)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundCollectionListRequest", required = true)
    public R<List<DelOutboundCollectionResponse>> collection(@RequestBody @Validated(DelOutboundGroup.Collection.class) DelOutboundCollectionListRequest request) {
        List<DelOutboundCollectionRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("999", "请求对象不能为空");
        }
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(requestList, DelOutboundDto.class);
        for (DelOutboundDto dto : dtoList) {
            dto.setOrderType(DelOutboundOrderTypeEnum.COLLECTION.getCode());
            dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_DOC);
        }
        List<DelOutboundAddResponse> responseList = delOutboundClientService.add(dtoList);
        return R.ok(BeanMapperUtil.mapList(responseList, DelOutboundCollectionResponse.class));
    }

    @PreAuthorize("hasAuthority('client')")
    @DeleteMapping("/cancel/collection")
    @ApiOperation(value = "#9 出库管理 - 取消单据（集运出库）", position = 501)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundCanceledRequest", required = true)
    public R<Integer> cancelCollection(@RequestBody @Validated DelOutboundCanceledRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("999", "订单号不能为空");
        }
        for (String orderNo : orderNos) {
            if (StringUtils.isEmpty(orderNo)) {
                throw new CommonException("999", "订单号值不能为空");
            }
        }
        DelOutboundCanceledDto canceledDto = new DelOutboundCanceledDto();
        canceledDto.setOrderNos(orderNos);
        canceledDto.setOrderType(DelOutboundOrderTypeEnum.COLLECTION);
        return R.ok(this.delOutboundClientService.canceled(canceledDto));
    }

    // @ApiOperation(value = "#10 出库管理 - 更新信息（集运出库）", position = 502)

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/batch")
    @ApiOperation(value = "#11 出库管理 - 订单创建（批量出库）", position = 600)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundBatchListRequest", required = true)
    public R<List<DelOutboundBatchResponse>> batch(@RequestBody @Validated(DelOutboundGroup.Batch.class) DelOutboundBatchListRequest request) {
        List<DelOutboundBatchRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("999", "请求对象不能为空");
        }
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(requestList, DelOutboundDto.class);
        for (DelOutboundDto dto : dtoList) {
            dto.setOrderType(DelOutboundOrderTypeEnum.BATCH.getCode());
            dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_DOC);
        }
        List<DelOutboundAddResponse> responseList = delOutboundClientService.add(dtoList);
        return R.ok(BeanMapperUtil.mapList(responseList, DelOutboundBatchResponse.class));
    }

    @PreAuthorize("hasAuthority('client')")
    @DeleteMapping("/packing/batch")
    @ApiOperation(value = "#12 出库管理 - 装箱结果（批量出库）", position = 601)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundPackingRequest", required = true)
    public R<List<DelOutboundSelfPickResponse>> packingBatch(@RequestBody @Validated DelOutboundPackingRequest request) {
        DelOutboundPacking delOutboundPacking = new DelOutboundPacking();
        delOutboundPacking.setOrderNo(request.getOrderNo());
        delOutboundPacking.setType(2);
        List<DelOutboundPacking> packingList = this.delOutboundClientService.queryList(delOutboundPacking);
        if (CollectionUtils.isEmpty(packingList)) {
            return R.ok(Collections.emptyList());
        }
        List<DelOutboundSelfPickResponse> responseList = BeanMapperUtil.mapList(packingList, DelOutboundSelfPickResponse.class);
        return R.ok(responseList);
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/label/batch")
    @ApiOperation(value = "#13 出库管理 - 标签上传（批量出库）", position = 602)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "orderNo", value = "单据号", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "文件", required = true, allowMultiple = true)
    })
    public R<Integer> labelBatch(@RequestParam("orderNo") String orderNo, HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");

        MultipartFile[] multipartFiles = new MultipartFile[]{multipartFile};
        R<List<BasAttachmentDataDTO>> listR = this.remoteAttachmentService.uploadAttachment(multipartFiles, AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL, "", "");
        List<BasAttachmentDataDTO> attachmentDataDTOList = R.getDataAndException(listR);

        List<AttachmentDataDTO> dataDTOList = BeanMapperUtil.mapList(attachmentDataDTOList, AttachmentDataDTO.class);

        DelOutboundUploadBoxLabelDto delOutboundUploadBoxLabelDto = new DelOutboundUploadBoxLabelDto();
        delOutboundUploadBoxLabelDto.setOrderNo(orderNo);
        delOutboundUploadBoxLabelDto.setBatchLabels(dataDTOList);
        delOutboundUploadBoxLabelDto.setAttachmentTypeEnum(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL);
        return R.ok(this.delOutboundClientService.uploadBoxLabel(delOutboundUploadBoxLabelDto));
    }

    @PreAuthorize("hasAuthority('client')")
    @DeleteMapping("/cancel/batch")
    @ApiOperation(value = "#14 出库管理 - 取消单据（批量出库）", position = 603)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundCanceledRequest", required = true)
    public R<Integer> cancelBatch(@RequestBody @Validated DelOutboundCanceledRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("999", "订单号不能为空");
        }
        DelOutboundCanceledDto canceledDto = new DelOutboundCanceledDto();
        canceledDto.setOrderNos(orderNos);
        canceledDto.setOrderType(DelOutboundOrderTypeEnum.BATCH);
        return R.ok(this.delOutboundClientService.canceled(canceledDto));
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/selfPick")
    @ApiOperation(value = "#15 出库管理 - 订单创建（自提出库）", position = 700)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundSelfPickListRequest", required = true)
    public R<List<DelOutboundSelfPickResponse>> selfPick(@RequestBody @Validated(DelOutboundGroup.SelfPick.class) DelOutboundSelfPickListRequest request) {
        List<DelOutboundSelfPickRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("999", "请求对象不能为空");
        }
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(requestList, DelOutboundDto.class);
        for (DelOutboundDto dto : dtoList) {
            dto.setOrderType(DelOutboundOrderTypeEnum.SELF_PICK.getCode());
            dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_DOC);
        }
        List<DelOutboundAddResponse> responseList = delOutboundClientService.add(dtoList);
        return R.ok(BeanMapperUtil.mapList(responseList, DelOutboundSelfPickResponse.class));
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/label/selfPick")
    @ApiOperation(value = "#16 出库管理 - 标签上传（自提出库）", position = 701)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "orderNo", value = "单据号", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "文件", required = true, allowMultiple = true)
    })
    public R<Integer> labelSelfPick(@RequestParam("orderNo") String orderNo, HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");

        MultipartFile[] multipartFiles = new MultipartFile[]{multipartFile};
        R<List<BasAttachmentDataDTO>> listR = this.remoteAttachmentService.uploadAttachment(multipartFiles, AttachmentTypeEnum.DEL_OUTBOUND_DOCUMENT, "", "");
        List<BasAttachmentDataDTO> attachmentDataDTOList = R.getDataAndException(listR);

        List<AttachmentDataDTO> dataDTOList = BeanMapperUtil.mapList(attachmentDataDTOList, AttachmentDataDTO.class);

        DelOutboundUploadBoxLabelDto delOutboundUploadBoxLabelDto = new DelOutboundUploadBoxLabelDto();
        delOutboundUploadBoxLabelDto.setOrderNo(orderNo);
        delOutboundUploadBoxLabelDto.setBatchLabels(dataDTOList);
        delOutboundUploadBoxLabelDto.setAttachmentTypeEnum(AttachmentTypeEnum.DEL_OUTBOUND_DOCUMENT);
        return R.ok(this.delOutboundClientService.uploadBoxLabel(delOutboundUploadBoxLabelDto));
    }

    @PreAuthorize("hasAuthority('client')")
    @DeleteMapping("/cancel/selfPick")
    @ApiOperation(value = "#17 出库管理 - 取消单据（自提出库）", position = 702)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundCanceledRequest", required = true)
    public R<Integer> cancelSelfPick(@RequestBody @Validated DelOutboundCanceledRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("999", "订单号不能为空");
        }
        DelOutboundCanceledDto canceledDto = new DelOutboundCanceledDto();
        canceledDto.setOrderNos(orderNos);
        canceledDto.setOrderType(DelOutboundOrderTypeEnum.SELF_PICK);
        return R.ok(this.delOutboundClientService.canceled(canceledDto));
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/destroy")
    @ApiOperation(value = "#18 出库管理 - 订单创建（销毁出库）", position = 800)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundDestroyListRequest", required = true)
    public R<List<DelOutboundDestroyResponse>> destroy(@RequestBody @Validated(DelOutboundGroup.Destroy.class) DelOutboundDestroyListRequest request) {
        List<DelOutboundDestroyRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("999", "请求对象不能为空");
        }
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(requestList, DelOutboundDto.class);
        for (DelOutboundDto dto : dtoList) {
            dto.setOrderType(DelOutboundOrderTypeEnum.DESTROY.getCode());
            dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_DOC);
        }
        List<DelOutboundAddResponse> responseList = delOutboundClientService.add(dtoList);
        return R.ok(BeanMapperUtil.mapList(responseList, DelOutboundDestroyResponse.class));
    }

    @PreAuthorize("hasAuthority('client')")
    @DeleteMapping("/cancel/destroy")
    @ApiOperation(value = "#19 出库管理 - 取消单据（销毁出库）", position = 801)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundCanceledRequest", required = true)
    public R<Integer> cancelDestroy(@RequestBody @Validated DelOutboundCanceledRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("999", "订单号不能为空");
        }
        DelOutboundCanceledDto canceledDto = new DelOutboundCanceledDto();
        canceledDto.setOrderNos(orderNos);
        canceledDto.setOrderType(DelOutboundOrderTypeEnum.DESTROY);
        return R.ok(this.delOutboundClientService.canceled(canceledDto));
    }
}
