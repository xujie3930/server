package com.szmsd.doc.api.delivery;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentDataDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.api.service.DelOutboundClientService;
import com.szmsd.delivery.domain.DelOutboundPacking;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
import com.szmsd.delivery.vo.DelOutboundLabelResponse;
import com.szmsd.delivery.vo.DelOutboundListVO;
import com.szmsd.doc.api.AssertUtil400;
import com.szmsd.doc.api.CountryCache;
import com.szmsd.doc.api.delivery.request.*;
import com.szmsd.doc.api.delivery.request.group.DelOutboundGroup;
import com.szmsd.doc.api.delivery.response.*;
import com.szmsd.doc.utils.AuthenticationUtil;
import com.szmsd.doc.utils.Base64CheckUtils;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.dto.ShipmentLabelChangeRequestDto;
import com.szmsd.http.vo.PricedProduct;
import com.szmsd.http.vo.ResponseVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author zhangyuyuan
 * @date 2021-07-28 16:05
 */
@Slf4j
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
            throw new CommonException("400", "SKU，产品属性信息不能全部为空");
        }
        // 验证国家是否存在
        if (null == CountryCache.getCountry(request.getCountryCode())) {
            throw new CommonException("400", "国家编码不存在");
        }
        String sellerCode = AuthenticationUtil.getSellerCode();
        DelOutboundOtherInServiceDto dto = BeanMapperUtil.map(request, DelOutboundOtherInServiceDto.class);
        dto.setClientCode(sellerCode);
        List<PricedProduct> productList = this.delOutboundClientService.inService(dto);
        if (CollectionUtils.isEmpty(productList)) {
            return R.ok();
        }
        return R.ok(BeanMapperUtil.mapList(productList, PricedProductResponse.class));
    }

    //@ApiIgnore
    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/package-transfer")
    @ApiOperation(value = "#2 出库管理 - 单据创建（转运出库）", position = 200)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundPackageTransferListRequest", required = true)
    public R<List<DelOutboundPackageTransferResponse>> packageTransfer(@RequestBody @Validated(value = {DelOutboundGroup.PackageTransfer.class}) DelOutboundPackageTransferListRequest request) {
        List<DelOutboundPackageTransferRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("400", "请求对象不能为空");
        }
        String sellerCode = AuthenticationUtil.getSellerCode();
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(requestList, DelOutboundDto.class);
        for (DelOutboundDto dto : dtoList) {
            dto.setSellerCode(sellerCode);
            dto.setOrderType(DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode());
            dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_DOC);
            this.setAddressCountry(dto);
        }
        List<DelOutboundAddResponse> responseList = delOutboundClientService.add(dtoList);
        return R.ok(BeanMapperUtil.mapList(responseList, DelOutboundPackageTransferResponse.class));
    }

    private void setAddressCountry(DelOutboundDto dto) {
        DelOutboundAddressDto address = dto.getAddress();
        if (null == address) {
            return;
        }
        String countryCode = address.getCountryCode();
        if (StringUtils.isEmpty(countryCode)) {
            return;
        }
        String country = CountryCache.getCountry(countryCode);
        if (null == country) {
            throw new CommonException("400", "国家编码[" + countryCode + "]不存在");
        }
        address.setCountry(country);
    }

    //@ApiIgnore
    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/package-transfer/label")
    @ApiOperation(value = "#3 出库管理 - 获取标签（转运出库）", position = 201, notes = "")
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundLabelRequest", required = true)
    public R<List<DelOutboundLabelResponse>> packageTransferLabel(@RequestBody @Validated DelOutboundLabelRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("400", "订单号不能为空");
        }
        DelOutboundLabelDto labelDto = new DelOutboundLabelDto();
        labelDto.setOrderNos(orderNos);
        return R.ok(this.delOutboundClientService.labelBase64(labelDto));
    }

    //@ApiIgnore
    @PreAuthorize("hasAuthority('client')")
    @DeleteMapping("/cancel/package-transfer")
    @ApiOperation(value = "#4 出库管理 - 取消单据（转运出库）", position = 202)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundCanceledRequest", required = true)
    public R<Integer> cancelPackageTransfer(@RequestBody @Validated DelOutboundCanceledRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("400", "订单号不能为空");
        }
        DelOutboundCanceledDto canceledDto = new DelOutboundCanceledDto();
        canceledDto.setOrderNos(orderNos);
        canceledDto.setOrderType(DelOutboundOrderTypeEnum.PACKAGE_TRANSFER);
        String sellerCode = AuthenticationUtil.getSellerCode();
        canceledDto.setSellerCode(sellerCode);
        return R.ok(this.delOutboundClientService.canceled(canceledDto));
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/page")
    @ApiOperation(value = "#5 出库管理 - 查询订单列表", position = 300)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundListQueryRequest", required = true)
    public TableDataInfo<DelOutboundListVO> page(@RequestBody DelOutboundListQueryRequest request) {
        DelOutboundListQueryDto dto = BeanMapperUtil.map(request, DelOutboundListQueryDto.class);
        return this.delOutboundFeignService.page(dto);
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/shipment")
    @ApiOperation(value = "#6 出库管理 - 订单创建（一件代发）", position = 400)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundShipmentListRequest", required = true)
    public R<List<DelOutboundShipmentResponse>> shipment(@RequestBody @Validated(DelOutboundGroup.Normal.class) DelOutboundShipmentListRequest request) {
        List<DelOutboundShipmentRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("400", "请求对象不能为空");
        }
        String sellerCode = AuthenticationUtil.getSellerCode();
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(requestList, DelOutboundDto.class);
        for (DelOutboundDto dto : dtoList) {
            dto.setSellerCode(sellerCode);
            dto.setOrderType(DelOutboundOrderTypeEnum.NORMAL.getCode());
            dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_DOC);
            this.setAddressCountry(dto);
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
            throw new CommonException("400", "订单号不能为空");
        }
        DelOutboundCanceledDto canceledDto = new DelOutboundCanceledDto();
        canceledDto.setOrderNos(orderNos);
        canceledDto.setOrderType(DelOutboundOrderTypeEnum.NORMAL);
        String sellerCode = AuthenticationUtil.getSellerCode();
        canceledDto.setSellerCode(sellerCode);
        return R.ok(this.delOutboundClientService.canceled(canceledDto));
    }

    //@ApiIgnore
    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/collection")
    @ApiOperation(value = "#8 出库管理 - 订单创建（集运出库）", position = 500)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundCollectionListRequest", required = true)
    public R<List<DelOutboundCollectionResponse>> collection(@RequestBody @Validated(DelOutboundGroup.Collection.class) DelOutboundCollectionListRequest request) {
        List<DelOutboundCollectionRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("400", "请求对象不能为空");
        }
        String sellerCode = AuthenticationUtil.getSellerCode();
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(requestList, DelOutboundDto.class);
        for (DelOutboundDto dto : dtoList) {
            dto.setSellerCode(sellerCode);
            dto.setOrderType(DelOutboundOrderTypeEnum.COLLECTION.getCode());
            dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_DOC);
            this.setAddressCountry(dto);
        }
        List<DelOutboundAddResponse> responseList = delOutboundClientService.add(dtoList);
        return R.ok(BeanMapperUtil.mapList(responseList, DelOutboundCollectionResponse.class));
    }

    //@ApiIgnore
    @PreAuthorize("hasAuthority('client')")
    @DeleteMapping("/cancel/collection")
    @ApiOperation(value = "#9 出库管理 - 取消单据（集运出库）", position = 501)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundCanceledRequest", required = true)
    public R<Integer> cancelCollection(@RequestBody @Validated DelOutboundCanceledRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("400", "订单号不能为空");
        }
        for (String orderNo : orderNos) {
            if (StringUtils.isEmpty(orderNo)) {
                throw new CommonException("400", "订单号值不能为空");
            }
        }
        DelOutboundCanceledDto canceledDto = new DelOutboundCanceledDto();
        canceledDto.setOrderNos(orderNos);
        canceledDto.setOrderType(DelOutboundOrderTypeEnum.COLLECTION);
        String sellerCode = AuthenticationUtil.getSellerCode();
        canceledDto.setSellerCode(sellerCode);
        return R.ok(this.delOutboundClientService.canceled(canceledDto));
    }

    // @ApiOperation(value = "#10 出库管理 - 更新信息（集运出库）", position = 502)

    //@ApiIgnore
//    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/batch")
    @ApiOperation(value = "#11 出库管理 - 订单创建（批量出库）", position = 600)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundBatchListRequest", required = true)
    public R<List<DelOutboundBatchResponse>> batch(@RequestBody @Validated(DelOutboundGroup.Batch.class) DelOutboundBatchListRequest request) {
        List<DelOutboundBatchRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("400", "请求对象不能为空");
        }
        AtomicInteger lineNum = new AtomicInteger(1);
        request.getRequestList().forEach(dto -> {
            if (StringUtils.isNotBlank(dto.getShipmentChannel()) && !"DMShipmentChannel".equals(dto.getShipmentChannel())) {
                AssertUtil400.isTrue(StringUtils.isNotBlank(dto.getFile()), "自提出库需要上传面单文件");
                byte[] bytes = Base64CheckUtils.checkAndConvert(dto.getFile());
                MultipartFile multipartFile = new MockMultipartFile("面单文件", "", "pdf", bytes);
                MultipartFile[] multipartFiles = new MultipartFile[]{multipartFile};
                R<List<BasAttachmentDataDTO>> listR = this.remoteAttachmentService.uploadAttachment(multipartFiles, AttachmentTypeEnum.PAYMENT_DOCUMENT, "", "");
                List<BasAttachmentDataDTO> attachmentDataDTOList = R.getDataAndException(listR);
                if (CollectionUtils.isNotEmpty(attachmentDataDTOList)) {
                    dto.setDocumentsFiles(BeanMapperUtil.mapList(attachmentDataDTOList, AttachmentDataDTO.class));
                }
            }
            Long boxNumber = dto.getBoxNumber();
            int thisLineNum = lineNum.getAndIncrement();
            // 验证 按包装要求需要填写包装详情
            if (null != dto.getIsPackingByRequired() && dto.getIsPackingByRequired()) {
                AssertUtil400.isTrue(null != boxNumber, String.format("第%s条数据,选择按要求装箱需要填写装箱数量", thisLineNum));
                AssertUtil400.isTrue(CollectionUtils.isNotEmpty(dto.getPackings()), String.format("第%s条数据选择按要求装箱需要填写装箱信息", thisLineNum));
                AtomicInteger innerLineNum = new AtomicInteger(1);
                dto.getPackings().forEach(x -> {
                    x.setQty(boxNumber);
                    int thisInnerLineNum = innerLineNum.getAndIncrement();
                    List<DelOutboundBatchPackingDetailRequest> details = x.getDetails();
                    AssertUtil400.isTrue(CollectionUtils.isNotEmpty(details), String.format("第%s条数据中的第%s条装箱明细未填写", thisLineNum, thisInnerLineNum));
                    AtomicInteger labelNo = new AtomicInteger(1);
                    List<String> collect = details.stream().map(DelOutboundBatchPackingDetailRequest::getSku).collect(Collectors.toList());
                    long count = collect.stream().distinct().count();
                    AssertUtil400.isTrue(collect.size() == count, "请检查是否装箱明细中存在相同的SKU");
                    details.forEach(z -> {
                        if (z.getNeedNewLabel()) {
                            // 判断是否需要标签
                            String newLabelCode = z.getNewLabelCode();
                            AssertUtil400.isTrue(StringUtils.isNotEmpty(newLabelCode), String.format("第%s条数据中的第%s条装箱明细中的第%s条新标签未填写", thisLineNum, thisInnerLineNum, labelNo));
                        }
                    });
                });
                // 生成对应的明细信息
                List<DelOutboundBatchSkuDetailRequest> details = dto.getPackings().stream().map(DelOutboundBatchPackingRequest::getDetails).flatMap(newDetail -> {
                    return newDetail.stream().map(d -> {
                        DelOutboundBatchSkuDetailRequest delOutboundBatchSkuDetailRequest = new DelOutboundBatchSkuDetailRequest();
                        BeanUtils.copyProperties(d, delOutboundBatchSkuDetailRequest);
                        //sku数量= 箱数*单个sku数量
                        delOutboundBatchSkuDetailRequest.setQty(d.getQty() * boxNumber);
                        return delOutboundBatchSkuDetailRequest;
                    });
                }).collect(Collectors.toList());
                dto.setDetails(details);
                // 按照包数生成对应包数的打包信息
                List<DelOutboundBatchPackingRequest> packings = dto.getPackings();
                for (int i = 0; i < dto.getBoxNumber()-1; i++) {
                    packings.add(packings.get(0));
                }
            } else {
                AssertUtil400.isTrue(CollectionUtils.isNotEmpty(dto.getDetails()), String.format("第%s条数据明细信息不能为空", thisLineNum));
            }
        });

        String sellerCode = AuthenticationUtil.getSellerCode();
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(requestList, DelOutboundDto.class);
        for (DelOutboundDto dto : dtoList) {
            dto.setSellerCode(sellerCode);
            dto.setOrderType(DelOutboundOrderTypeEnum.BATCH.getCode());
            dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_DOC);
            // 验证
            if ("058001".equals(dto.getDeliveryMethod())) {
                DelOutboundAddressDto address = dto.getAddress();
                if (null == address) {
                    throw new CommonException("400", "地址信息不能为空");
                }
                if (StringUtils.isEmpty(address.getConsignee())) {
                    throw new CommonException("400", "收货人不能为空");
                }
                if (StringUtils.isEmpty(address.getStreet1())) {
                    throw new CommonException("400", "街道1不能为空");
                }
                if (StringUtils.isEmpty(address.getCountryCode())) {
                    throw new CommonException("400", "国家不能为空");
                }
                if (StringUtils.isEmpty(address.getCountry())) {
                    throw new CommonException("400", "国家名称不能为空");
                }
                if (StringUtils.isEmpty(address.getPostCode())) {
                    throw new CommonException("400", "邮编不能为空");
                }
            }
            // 验证
                //if (null != dto.getIsLabelBox() && dto.getIsLabelBox()) {
                for (DelOutboundDetailDto detail : dto.getDetails()) {
                    if (detail.getNeedNewLabel() && StringUtils.isBlank(detail.getNewLabelCode())) {
                        throw new CommonException("400", "新标签不能为空");
                    }
                }
                //}
            // 验证
            if (StringUtils.isNotBlank(dto.getShipmentChannel())) {
                //DMShipmentChannel 渠道发货，提货方式、时间、供应商，快递信息不需要必填
                if (!"DMShipmentChannel".equalsIgnoreCase(dto.getShipmentChannel().trim())){
                    AssertUtil400.isTrue(StringUtils.isNotBlank(dto.getDeliveryMethod()),"提货方式不能为空");
                    AssertUtil400.isTrue(Objects.nonNull(dto.getDeliveryTime()),"提货时间不能为空");
                    AssertUtil400.isTrue(StringUtils.isNotBlank(dto.getDeliveryAgent()),"提货供应商/快递商不能为空");
                    //AssertUtil400.isTrue(StringUtils.isNotBlank(dto.getDeliveryInfo()),"提货/快递信息不能为空");
                }
            }
            // 验证 按包装要求需要填写包装详情
            if (null != dto.getIsPackingByRequired() && dto.getIsPackingByRequired()) {
                List<DelOutboundPackingDto> packings = dto.getPackings();
                AssertUtil400.isTrue(CollectionUtils.isNotEmpty(packings), "按要求装箱需要填写装箱信息");
            }else {
                AssertUtil400.isTrue(CollectionUtils.isNotEmpty(dto.getDetails()), "明细信息不能为空");
            }
            this.setAddressCountry(dto);
        }
        List<DelOutboundAddResponse> responseList = delOutboundClientService.add(dtoList);
        return R.ok(BeanMapperUtil.mapList(responseList, DelOutboundBatchResponse.class));
    }

    //@ApiIgnore
    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/packing/batch")
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

    //@ApiIgnore
//    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/label/batch")
    @ApiOperation(value = "#13 出库管理 - 标签上传（批量出库）", position = 602)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "orderNo", value = "单据号", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "文件", required = true, allowMultiple = true)
    })
    public R<Integer> labelBatch(@RequestParam("orderNo") String orderNo, HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");
        AssertUtil400.isTrue(StringUtils.isNotBlank(orderNo), "单据号不能为空");
        AssertUtil400.isTrue(null != multipartFile, "文件不能为空");
        TableDataInfo<DelOutboundListVO> delOutboundListTableDataInfo = getDelOutboundListTableDataInfo(orderNo, DelOutboundOrderTypeEnum.BATCH,DelOutboundStateEnum.REVIEWED.getCode(), DelOutboundStateEnum.AUDIT_FAILED.getCode(), DelOutboundStateEnum.DELIVERED.getCode(), DelOutboundStateEnum.PROCESSING.getCode(), DelOutboundStateEnum.NOTIFY_WHSE_PROCESSING.getCode(), DelOutboundStateEnum.WHSE_PROCESSING.getCode());
        AssertUtil400.isTrue(!(null == delOutboundListTableDataInfo || delOutboundListTableDataInfo.getTotal() == 0), "单据号不存在");
        MultipartFile[] multipartFiles = new MultipartFile[]{multipartFile};
        R<List<BasAttachmentDataDTO>> listR = this.remoteAttachmentService.uploadAttachment(multipartFiles, AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL, "", "");
        List<BasAttachmentDataDTO> attachmentDataDTOList = R.getDataAndException(listR);

        List<AttachmentDataDTO> dataDTOList = BeanMapperUtil.mapList(attachmentDataDTOList, AttachmentDataDTO.class);

        DelOutboundUploadBoxLabelDto delOutboundUploadBoxLabelDto = new DelOutboundUploadBoxLabelDto();
        delOutboundUploadBoxLabelDto.setOrderNo(orderNo);
        delOutboundUploadBoxLabelDto.setBatchLabels(dataDTOList);
        delOutboundUploadBoxLabelDto.setAttachmentTypeEnum(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL);
        int i = this.delOutboundClientService.uploadBoxLabel(delOutboundUploadBoxLabelDto);
        DelOutboundListVO delOutboundVO= delOutboundListTableDataInfo.getRows().get(0);

        //提成功后 发起提审 //待提审审核失败才发起提审
        if (DelOutboundStateEnum.REVIEWED.getCode().equals(delOutboundVO.getState())
                || DelOutboundStateEnum.AUDIT_FAILED.getCode().equals(delOutboundVO.getState())) {
            DelOutboundBringVerifyDto delOutboundBringVerifyDto = new DelOutboundBringVerifyDto();
            Long id = delOutboundVO.getId();
            delOutboundBringVerifyDto.setIds(Collections.singletonList(id));
            delOutboundClientService.bringVerify(delOutboundBringVerifyDto);
        }else {
            try {
                // 待提审/审核失败单据未推送给wms，无法更新箱标文件 其他情况则需要更新单据文件
                byte[] byteArray = multipartFile.getBytes();
                String encode = Base64.encode(byteArray);
                ShipmentLabelChangeRequestDto shipmentLabelChangeRequestDto = new ShipmentLabelChangeRequestDto();
                shipmentLabelChangeRequestDto.setWarehouseCode(delOutboundVO.getWarehouseCode());
                shipmentLabelChangeRequestDto.setOrderNo(delOutboundVO.getOrderNo());
                shipmentLabelChangeRequestDto.setLabelType("ShipmentLabel");
                shipmentLabelChangeRequestDto.setLabel(encode);
                IHtpOutboundClientService htpOutboundClientService = SpringUtils.getBean(IHtpOutboundClientService.class);
                ResponseVO responseVO = htpOutboundClientService.shipmentLabel(shipmentLabelChangeRequestDto);
                log.info("更新标签文件：单号：{}-{}",delOutboundVO.getOrderNo(), JSONObject.toJSONString(responseVO));
                if (null == responseVO || null == responseVO.getSuccess()) {
                    throw new CommonException("400", "更新标签失败");
                }
                if (!responseVO.getSuccess()) {
                    throw new CommonException("400", StringUtils.nvl(responseVO.getMessage(), "更新标签失败2"));
                }
            } catch (IOException e) {
                throw new CommonException("400", "读取标签文件失败");
            }
            //更新发货指令
            delOutboundClientService.updateShipmentLabel(Collections.singletonList(delOutboundVO.getId() + ""));
        }
        return R.ok(i);
    }

    /**
     * 校验单据是否存在
     * @param orderNo
     * @return
     */
    public boolean verifyOrderSelf(String orderNo,DelOutboundOrderTypeEnum orderType,String... state) {
        TableDataInfo<DelOutboundListVO> page = getDelOutboundListTableDataInfo(orderNo, orderType,state);
        if (null == page || page.getTotal() == 0) {
            return false;
        }
        return true;
    }

    private TableDataInfo<DelOutboundListVO> getDelOutboundListTableDataInfo(String orderNo, DelOutboundOrderTypeEnum orderType,String... state) {
        DelOutboundListQueryDto delOutboundListQueryDto = new DelOutboundListQueryDto();
        delOutboundListQueryDto.setOrderNo(orderNo);
        delOutboundListQueryDto.setOrderType(orderType.getCode());
        delOutboundListQueryDto.setCustomCode(AuthenticationUtil.getSellerCode());
        if (state != null) {
            delOutboundListQueryDto.setState(String.join(",", state));
        } else {
            //待发货 审核失败 待提审
            delOutboundListQueryDto.setState(DelOutboundStateEnum.AUDIT_FAILED.getCode() + "," + DelOutboundStateEnum.REVIEWED.getCode());
        }
       TableDataInfo<DelOutboundListVO> page = this.delOutboundFeignService.page(delOutboundListQueryDto);
        return page;
    }

    //@ApiIgnore
    @PreAuthorize("hasAuthority('client')")
    @DeleteMapping("/cancel/batch")
    @ApiOperation(value = "#14 出库管理 - 取消单据（批量出库）", position = 603)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundCanceledRequest", required = true)
    public R<Integer> cancelBatch(@RequestBody @Validated DelOutboundCanceledRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("400", "订单号不能为空");
        }
        DelOutboundCanceledDto canceledDto = new DelOutboundCanceledDto();
        canceledDto.setOrderNos(orderNos);
        canceledDto.setOrderType(DelOutboundOrderTypeEnum.BATCH);
        String sellerCode = AuthenticationUtil.getSellerCode();
        canceledDto.setSellerCode(sellerCode);
        return R.ok(this.delOutboundClientService.canceled(canceledDto));
    }

    //@ApiIgnore
    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/selfPick")
    @ApiOperation(value = "#15 出库管理 - 订单创建（自提出库）", position = 700)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundSelfPickListRequest", required = true)
    public R<List<DelOutboundSelfPickResponse>> selfPick(@RequestBody @Validated(DelOutboundGroup.SelfPick.class) DelOutboundSelfPickListRequest request) {
        List<DelOutboundSelfPickRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("400", "请求对象不能为空");
        }
        String sellerCode = AuthenticationUtil.getSellerCode();
        requestList.forEach(x -> {
            if (StringUtils.isNotBlank(x.getFile())) {
                byte[] bytes = Base64CheckUtils.checkAndConvert(x.getFile());
                MultipartFile multipartFile = new MockMultipartFile("面单文件", "", "pdf", bytes);
                MultipartFile[] multipartFiles = new MultipartFile[]{multipartFile};
                R<List<BasAttachmentDataDTO>> listR = this.remoteAttachmentService.uploadAttachment(multipartFiles, AttachmentTypeEnum.DEL_OUTBOUND_DOCUMENT, "", "");
                List<BasAttachmentDataDTO> attachmentDataDTOList = R.getDataAndException(listR);
                if (CollectionUtils.isNotEmpty(attachmentDataDTOList)) {
                    x.setDocumentsFiles(BeanMapperUtil.mapList(attachmentDataDTOList, AttachmentDataDTO.class));
                }
            }
        });
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(requestList, DelOutboundDto.class);
        for (DelOutboundDto dto : dtoList) {
            dto.setSellerCode(sellerCode);
            dto.setOrderType(DelOutboundOrderTypeEnum.SELF_PICK.getCode());
            dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_DOC);
            this.setAddressCountry(dto);
        }
        List<DelOutboundAddResponse> responseList = delOutboundClientService.add(dtoList);
        return R.ok(BeanMapperUtil.mapList(responseList, DelOutboundSelfPickResponse.class));
    }

    //@ApiIgnore
    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/label/selfPick")
    @ApiOperation(value = "#16 出库管理 - 标签上传（自提出库--修改标签）", position = 701)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "orderNo", value = "单据号", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "文件", required = true, allowMultiple = true)
    })
    public R<Integer> labelSelfPick(@RequestParam("orderNo") String orderNo, HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");
        AssertUtil400.isTrue(StringUtils.isNotBlank(orderNo),"单据号不能为空");
        AssertUtil400.isTrue(verifyOrderSelf(orderNo,DelOutboundOrderTypeEnum.SELF_PICK),"有部分单号已经开始操作，不能上传");
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

    //@ApiIgnore
    @PreAuthorize("hasAuthority('client')")
    @DeleteMapping("/cancel/selfPick")
    @ApiOperation(value = "#17 出库管理 - 取消单据（自提出库）", position = 702)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundCanceledRequest", required = true)
    public R<Integer> cancelSelfPick(@RequestBody @Validated DelOutboundCanceledRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("400", "订单号不能为空");
        }
        DelOutboundCanceledDto canceledDto = new DelOutboundCanceledDto();
        canceledDto.setOrderNos(orderNos);
        canceledDto.setOrderType(DelOutboundOrderTypeEnum.SELF_PICK);
        String sellerCode = AuthenticationUtil.getSellerCode();
        canceledDto.setSellerCode(sellerCode);
        return R.ok(this.delOutboundClientService.canceled(canceledDto));
    }

    //@ApiIgnore
    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/destroy")
    @ApiOperation(value = "#18 出库管理 - 订单创建（销毁出库）", position = 800)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundDestroyListRequest", required = true)
    public R<List<DelOutboundDestroyResponse>> destroy(@RequestBody @Validated(DelOutboundGroup.Destroy.class) DelOutboundDestroyListRequest request) {
        List<DelOutboundDestroyRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("400", "请求对象不能为空");
        }
        String sellerCode = AuthenticationUtil.getSellerCode();
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(requestList, DelOutboundDto.class);
        for (DelOutboundDto dto : dtoList) {
            dto.setSellerCode(sellerCode);
            dto.setOrderType(DelOutboundOrderTypeEnum.DESTROY.getCode());
            dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_DOC);
            this.setAddressCountry(dto);
        }
        List<DelOutboundAddResponse> responseList = delOutboundClientService.add(dtoList);
        return R.ok(BeanMapperUtil.mapList(responseList, DelOutboundDestroyResponse.class));
    }

    //@ApiIgnore
    @PreAuthorize("hasAuthority('client')")
    @DeleteMapping("/cancel/destroy")
    @ApiOperation(value = "#19 出库管理 - 取消单据（销毁出库）", position = 801)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundCanceledRequest", required = true)
    public R<Integer> cancelDestroy(@RequestBody @Validated DelOutboundCanceledRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("400", "订单号不能为空");
        }
        DelOutboundCanceledDto canceledDto = new DelOutboundCanceledDto();
        canceledDto.setOrderNos(orderNos);
        canceledDto.setOrderType(DelOutboundOrderTypeEnum.DESTROY);
        String sellerCode = AuthenticationUtil.getSellerCode();
        canceledDto.setSellerCode(sellerCode);
        return R.ok(this.delOutboundClientService.canceled(canceledDto));
    }
}
