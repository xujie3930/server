package com.szmsd.doc.api.delivery;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
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
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.api.service.DelOutboundClientService;
import com.szmsd.delivery.domain.DelOutboundPacking;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.vo.*;
import com.szmsd.doc.api.AssertUtil400;
import com.szmsd.doc.api.CountryCache;
import com.szmsd.doc.api.delivery.request.*;
import com.szmsd.doc.api.delivery.request.group.DelOutboundGroup;
import com.szmsd.doc.api.delivery.response.*;
import com.szmsd.doc.utils.AuthenticationUtil;
import com.szmsd.doc.utils.Base64CheckUtils;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.PricedProductSearchCriteria;
import com.szmsd.http.dto.ShipmentLabelChangeRequestDto;
import com.szmsd.http.vo.PricedProduct;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.track.api.feign.TrackFeignService;
import com.szmsd.track.dto.TrackMainDocCommonDto;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
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
@Api(tags = {"????????????"})
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
    @Autowired
    private IHtpPricedProductClientService htpPricedProductClientService;

    @Autowired
    private TrackFeignService trackFeignService;

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/priced-product")
    @ApiOperation(value = "#1.1 ???????????? - ??????????????????", position = 100, notes = "????????????")
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "PricedProductRequest", required = true)
    public R<List<PricedProductResponse>> pricedProduct(@RequestBody @Validated PricedProductRequest request) {
        /*if (CollectionUtils.isEmpty(request.getSkus()) && CollectionUtils.isEmpty(request.getProductAttributes())) {
            throw new CommonException("400", "SKU???????????????????????????????????????");
        }
        // ????????????????????????
        if (StringUtils.isNotEmpty(request.getCountryCode()) && null == CountryCache.getCountry(request.getCountryCode())) {
            throw new CommonException("400", "?????????????????????");
        }*/
        // String sellerCode = AuthenticationUtil.getSellerCode();
        DelOutboundOtherInServiceDto dto = BeanMapperUtil.map(request, DelOutboundOtherInServiceDto.class);
        // dto.setClientCode(sellerCode);
        List<PricedProduct> productList = this.delOutboundClientService.inService(dto);
        if (CollectionUtils.isEmpty(productList)) {
            return R.ok();
        }
        return R.ok(BeanMapperUtil.mapList(productList, PricedProductResponse.class));
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/priced-product-page")
    @ApiOperation(value = "#1.2 ???????????? - ????????????????????????", position = 101, notes = "????????????")
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "PricedProductSearchCriteria", required = true)
    public R<PageVO<PricedProduct>> pricedProductPage(@RequestBody @Validated PricedProductSearchCriteria request) {
        return R.ok(this.htpPricedProductClientService.pageResult(request));
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/package-transfer")
    @ApiOperation(value = "#2 ???????????? - ??????????????????????????????", position = 200)
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "DelOutboundPackageTransferListRequest", required = true)
    public R<List<DelOutboundPackageTransferResponse>> packageTransfer(@RequestBody @Validated(value = {DelOutboundGroup.PackageTransfer.class}) DelOutboundPackageTransferListRequest request) {
        List<DelOutboundPackageTransferRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("400", "The request object cannot be empty");
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
        log.info("responseList:{}",JSON.toJSONString(responseList));
        return R.ok(BeanMapperUtil.mapList(responseList, DelOutboundPackageTransferResponse.class));
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/package-transfer-sync")
    @ApiOperation(value = "#2.1 ???????????? - ??????????????????????????????????????????trackingNo", position = 200)
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "DelOutboundPackageTransferListRequest", required = true)
    public R<List<DelOutboundPackageTransferResponse>> packageTransferSync(@RequestBody @Validated(value = {DelOutboundGroup.PackageTransfer.class}) DelOutboundPackageTransferListRequest request) {
        List<DelOutboundPackageTransferRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("400", "The request object cannot be empty");
        }
        String sellerCode = AuthenticationUtil.getSellerCode();
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(requestList, DelOutboundDto.class);
        for (DelOutboundDto dto : dtoList) {
            dto.setSellerCode(sellerCode);
            dto.setOrderType(DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode());
            dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_DOC);
            dto.setSyncTrackingNoState(1);
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
            throw new CommonException("400", "Country code [" + countryCode + "] non-existent");
        }
        address.setCountry(country);
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/package-transfer/label")
    @ApiOperation(value = "#3 ???????????? - ??????????????????????????????", position = 201, notes = "")
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "DelOutboundLabelRequest", required = true)
    public R<List<DelOutboundLabelResponse>> packageTransferLabel(@RequestBody @Validated DelOutboundLabelRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("400", "Order No. cannot be empty");
        }
        DelOutboundLabelDto labelDto = new DelOutboundLabelDto();
        labelDto.setOrderNos(orderNos);
        labelDto.setType(request.getType());
        return R.ok(this.delOutboundClientService.labelBase64(labelDto));
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/bringVerifyByOrderNo")
    @ApiOperation(value = "#23 ???????????? - ??????", position = 600)
    public R<List<DelOutboundBringVerifyVO>> bringVerifyByOrderNo(@RequestBody @Validated DelOutboundBringVerifyNoDto dto) {
        return R.ok(this.delOutboundClientService.bringVerifyByOrderNo(dto));
    }



    @PreAuthorize("hasAuthority('client')")
    @DeleteMapping("/cancel/package-transfer")
    @ApiOperation(value = "#4 ???????????? - ??????????????????????????????", position = 202)
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "DelOutboundCanceledRequest", required = true)
    public R<Integer> cancelPackageTransfer(@RequestBody @Validated DelOutboundCanceledRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("400", "Order No. cannot be empty");
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
    @ApiOperation(value = "#5 ???????????? - ??????????????????", position = 300)
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "DelOutboundListQueryRequest", required = true)
    public TableDataInfo<DelOutboundListVO> page(@RequestBody DelOutboundListQueryRequest request) {
        DelOutboundListQueryDto dto = BeanMapperUtil.map(request, DelOutboundListQueryDto.class);
        return this.delOutboundFeignService.page(dto);
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/shipment")
    @ApiOperation(value = "#6 ???????????? - ??????????????????????????????", position = 400)
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "DelOutboundShipmentListRequest", required = true)
    public R<List<DelOutboundShipmentResponse>> shipment(@RequestBody @Validated(DelOutboundGroup.Normal.class) DelOutboundShipmentListRequest request) {
        List<DelOutboundShipmentRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("400", "The request object cannot be empty");
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
    @ApiOperation(value = "#7 ???????????? - ??????????????????????????????", position = 401)
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "DelOutboundCanceledRequest", required = true)
    public R<Integer> cancelShipment(@RequestBody @Validated DelOutboundCanceledRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("400", "Order No. cannot be empty");
        }
        DelOutboundCanceledDto canceledDto = new DelOutboundCanceledDto();
        canceledDto.setOrderNos(orderNos);
        canceledDto.setOrderType(DelOutboundOrderTypeEnum.NORMAL);
        String sellerCode = AuthenticationUtil.getSellerCode();
        canceledDto.setSellerCode(sellerCode);
        return R.ok(this.delOutboundClientService.canceled(canceledDto));
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/collection")
    @ApiOperation(value = "#8 ???????????? - ??????????????????????????????", position = 500)
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "DelOutboundCollectionListRequest", required = true)
    public R<List<DelOutboundCollectionResponse>> collection(@RequestBody @Validated(DelOutboundGroup.Collection.class) DelOutboundCollectionListRequest request) {
        List<DelOutboundCollectionRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("400", "The request object cannot be empty");
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

    @PreAuthorize("hasAuthority('client')")
    @DeleteMapping("/cancel/collection")
    @ApiOperation(value = "#9 ???????????? - ??????????????????????????????", position = 501)
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "DelOutboundCanceledRequest", required = true)
    public R<Integer> cancelCollection(@RequestBody @Validated DelOutboundCanceledRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("400", "Order No. cannot be empty");
        }
        for (String orderNo : orderNos) {
            if (StringUtils.isEmpty(orderNo)) {
                throw new CommonException("400", "Order No. cannot be empty");
            }
        }
        DelOutboundCanceledDto canceledDto = new DelOutboundCanceledDto();
        canceledDto.setOrderNos(orderNos);
        canceledDto.setOrderType(DelOutboundOrderTypeEnum.COLLECTION);
        String sellerCode = AuthenticationUtil.getSellerCode();
        canceledDto.setSellerCode(sellerCode);
        return R.ok(this.delOutboundClientService.canceled(canceledDto));
    }

    // @ApiOperation(value = "#10 ???????????? - ??????????????????????????????", position = 502)

    //@ApiIgnore
//    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/batch")
    @ApiOperation(value = "#11 ???????????? - ??????????????????????????????", position = 600)
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "DelOutboundBatchListRequest", required = true)
    public R<List<DelOutboundBatchResponse>> batch(@RequestBody @Validated(DelOutboundGroup.Batch.class) DelOutboundBatchListRequest request) {
        List<DelOutboundBatchRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("400", "The request object cannot be empty");
        }
        AtomicInteger lineNum = new AtomicInteger(1);
        request.getRequestList().forEach(dto -> {
            if (StringUtils.isNotBlank(dto.getShipmentChannel()) && !"DMShipmentChannel".equals(dto.getShipmentChannel())) {
                AssertUtil400.isTrue(StringUtils.isNotBlank(dto.getFile()), "????????????????????????????????????");
                AssertUtil400.isTrue(StringUtils.isNotBlank(dto.getFileName()), "???????????????????????????");
                byte[] bytes = Base64CheckUtils.checkAndConvert(dto.getFile());
                MultipartFile multipartFile = new MockMultipartFile("????????????", dto.getFileName(), "pdf", bytes);
                MultipartFile[] multipartFiles = new MultipartFile[]{multipartFile};
                R<List<BasAttachmentDataDTO>> listR = this.remoteAttachmentService.uploadAttachment(multipartFiles, AttachmentTypeEnum.PAYMENT_DOCUMENT, "", "");
                List<BasAttachmentDataDTO> attachmentDataDTOList = R.getDataAndException(listR);
                if (CollectionUtils.isNotEmpty(attachmentDataDTOList)) {
                    dto.setDocumentsFiles(BeanMapperUtil.mapList(attachmentDataDTOList, AttachmentDataDTO.class));
                }
            }
            Long boxNumber = dto.getBoxNumber();
            int thisLineNum = lineNum.getAndIncrement();
            // ?????? ???????????????????????????????????????
            if (null != dto.getIsPackingByRequired() && dto.getIsPackingByRequired()) {
                AssertUtil400.isTrue(null != boxNumber, String.format("???%s?????????,?????????????????????????????????????????????", thisLineNum));
                AssertUtil400.isTrue(CollectionUtils.isNotEmpty(dto.getPackings()), String.format("???%s??????????????????????????????????????????????????????", thisLineNum));
                AtomicInteger innerLineNum = new AtomicInteger(1);
                dto.getPackings().forEach(x -> {
                    x.setQty(boxNumber);
                    int thisInnerLineNum = innerLineNum.getAndIncrement();
                    List<DelOutboundBatchPackingDetailRequest> details = x.getDetails();
                    AssertUtil400.isTrue(CollectionUtils.isNotEmpty(details), String.format("???%s??????????????????%s????????????????????????", thisLineNum, thisInnerLineNum));
                    AtomicInteger labelNo = new AtomicInteger(1);
                    List<String> collect = details.stream().map(DelOutboundBatchPackingDetailRequest::getSku).collect(Collectors.toList());
                    long count = collect.stream().distinct().count();
                    AssertUtil400.isTrue(collect.size() == count, "?????????????????????????????????????????????SKU");
                    details.forEach(z -> {
                        if (z.getNeedNewLabel()) {
                            // ????????????????????????
                            String newLabelCode = z.getNewLabelCode();
                            AssertUtil400.isTrue(StringUtils.isNotEmpty(newLabelCode), String.format("???%s??????????????????%s????????????????????????%s?????????????????????", thisLineNum, thisInnerLineNum, labelNo));
                        }
                    });
                });
                // ???????????????????????????
                List<DelOutboundBatchSkuDetailRequest> details = dto.getPackings().stream().map(DelOutboundBatchPackingRequest::getDetails).flatMap(newDetail -> {
                    return newDetail.stream().map(d -> {
                        DelOutboundBatchSkuDetailRequest delOutboundBatchSkuDetailRequest = new DelOutboundBatchSkuDetailRequest();
                        BeanUtils.copyProperties(d, delOutboundBatchSkuDetailRequest);
                        //sku??????= ??????*??????sku??????
                        delOutboundBatchSkuDetailRequest.setQty(d.getQty() * boxNumber);
                        return delOutboundBatchSkuDetailRequest;
                    });
                }).collect(Collectors.toList());
                dto.setDetails(details);
                // ?????????????????????????????????????????????
                List<DelOutboundBatchPackingRequest> packings = dto.getPackings();
                for (int i = 0; i < dto.getBoxNumber() - 1; i++) {
                    packings.add(packings.get(0));
                }
            } else {
                AssertUtil400.isTrue(CollectionUtils.isNotEmpty(dto.getDetails()), String.format("???%s?????????????????????????????????", thisLineNum));
            }
        });

        String sellerCode = AuthenticationUtil.getSellerCode();
        List<DelOutboundDto> dtoList = BeanMapperUtil.mapList(requestList, DelOutboundDto.class);
        for (DelOutboundDto dto : dtoList) {
            dto.setSellerCode(sellerCode);
            dto.setOrderType(DelOutboundOrderTypeEnum.BATCH.getCode());
            dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_DOC);
            // ??????
            if ("058001".equals(dto.getDeliveryMethod())) {
                DelOutboundAddressDto address = dto.getAddress();
                if (null == address) {
                    throw new CommonException("400", "????????????????????????");
                }
                if (StringUtils.isEmpty(address.getConsignee())) {
                    throw new CommonException("400", "?????????????????????");
                }
                if (StringUtils.isEmpty(address.getStreet1())) {
                    throw new CommonException("400", "??????1????????????");
                }
                if (StringUtils.isEmpty(address.getCountryCode())) {
                    throw new CommonException("400", "??????????????????");
                }
                if (StringUtils.isEmpty(address.getCountry())) {
                    throw new CommonException("400", "????????????????????????");
                }
                if (StringUtils.isEmpty(address.getPostCode())) {
                    throw new CommonException("400", "??????????????????");
                }
            }
            // ??????
            //if (null != dto.getIsLabelBox() && dto.getIsLabelBox()) {
            for (DelOutboundDetailDto detail : dto.getDetails()) {
                if (detail.getNeedNewLabel() && StringUtils.isBlank(detail.getNewLabelCode())) {
                    throw new CommonException("400", "?????????????????????");
                }
            }
            //}
            // ??????
            if (StringUtils.isNotBlank(dto.getShipmentChannel())) {
                //DMShipmentChannel ??????????????????????????????????????????????????????????????????????????????
                if (!"DMShipmentChannel".equalsIgnoreCase(dto.getShipmentChannel().trim())) {
                    AssertUtil400.isTrue(StringUtils.isNotBlank(dto.getDeliveryMethod()), "????????????????????????");
                    AssertUtil400.isTrue(Objects.nonNull(dto.getDeliveryTime()), "????????????????????????");
                    AssertUtil400.isTrue(StringUtils.isNotBlank(dto.getDeliveryAgent()), "???????????????/?????????????????????");
                    //AssertUtil400.isTrue(StringUtils.isNotBlank(dto.getDeliveryInfo()),"??????/????????????????????????");
                }
            }
            // ?????? ???????????????????????????????????????
            if (null != dto.getIsPackingByRequired() && dto.getIsPackingByRequired()) {
                List<DelOutboundPackingDto> packings = dto.getPackings();
                AssertUtil400.isTrue(CollectionUtils.isNotEmpty(packings), "???????????????????????????????????????");
            } else {
                AssertUtil400.isTrue(CollectionUtils.isNotEmpty(dto.getDetails()), "????????????????????????");
                dto.setPackings(null);
            }
            this.setAddressCountry(dto);
        }
        List<DelOutboundAddResponse> responseList = delOutboundClientService.add(dtoList);
        return R.ok(BeanMapperUtil.mapList(responseList, DelOutboundBatchResponse.class));
    }

    //@ApiIgnore
    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/packing/batch")
    @ApiOperation(value = "#12 ???????????? - ??????????????????????????????", position = 601)
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "DelOutboundPackingRequest", required = true)
    public R<List<DelOutboundPackingResponse>> packingBatch(@RequestBody @Validated DelOutboundPackingRequest request) {
        DelOutboundPacking delOutboundPacking = new DelOutboundPacking();
        delOutboundPacking.setOrderNo(request.getOrderNo());
        delOutboundPacking.setType(2);
        List<DelOutboundPackingVO> packingList = this.delOutboundClientService.listByOrderNo(delOutboundPacking);
        if (CollectionUtils.isEmpty(packingList)) {
            return R.ok(Collections.emptyList());
        }
        List<DelOutboundPackingResponse> responseList = BeanMapperUtil.mapList(packingList, DelOutboundPackingResponse.class);
        return R.ok(responseList);
    }

    //@ApiIgnore
//    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/label/batch")
    @ApiOperation(value = "#13 ???????????? - ??????????????????????????????", position = 602)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "orderNo", value = "?????????", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "??????", required = true, allowMultiple = true)
    })
    public R<Integer> labelBatch(@RequestParam("orderNo") String orderNo, HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");
        AssertUtil400.isTrue(StringUtils.isNotBlank(orderNo), "?????????????????????");
        AssertUtil400.isTrue(null != multipartFile, "??????????????????");
        TableDataInfo<DelOutboundListVO> delOutboundListTableDataInfo = getDelOutboundListTableDataInfo(orderNo, DelOutboundOrderTypeEnum.BATCH, DelOutboundStateEnum.REVIEWED.getCode(), DelOutboundStateEnum.AUDIT_FAILED.getCode(), DelOutboundStateEnum.DELIVERED.getCode(), DelOutboundStateEnum.PROCESSING.getCode(), DelOutboundStateEnum.NOTIFY_WHSE_PROCESSING.getCode(), DelOutboundStateEnum.WHSE_PROCESSING.getCode());
        AssertUtil400.isTrue(!(null == delOutboundListTableDataInfo || delOutboundListTableDataInfo.getTotal() == 0), "??????????????????");
        MultipartFile[] multipartFiles = new MultipartFile[]{multipartFile};
        R<List<BasAttachmentDataDTO>> listR = this.remoteAttachmentService.uploadAttachment(multipartFiles, AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL, "", "");
        List<BasAttachmentDataDTO> attachmentDataDTOList = R.getDataAndException(listR);

        List<AttachmentDataDTO> dataDTOList = BeanMapperUtil.mapList(attachmentDataDTOList, AttachmentDataDTO.class);

        DelOutboundUploadBoxLabelDto delOutboundUploadBoxLabelDto = new DelOutboundUploadBoxLabelDto();
        delOutboundUploadBoxLabelDto.setOrderNo(orderNo);
        delOutboundUploadBoxLabelDto.setBatchLabels(dataDTOList);
        delOutboundUploadBoxLabelDto.setAttachmentTypeEnum(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL);
        int i = this.delOutboundClientService.uploadBoxLabel(delOutboundUploadBoxLabelDto);
        DelOutboundListVO delOutboundVO = delOutboundListTableDataInfo.getRows().get(0);

        //???????????? ???????????? //????????????????????????????????????
        if (DelOutboundStateEnum.REVIEWED.getCode().equals(delOutboundVO.getState())
                || DelOutboundStateEnum.AUDIT_FAILED.getCode().equals(delOutboundVO.getState())) {
            DelOutboundBringVerifyDto delOutboundBringVerifyDto = new DelOutboundBringVerifyDto();
            Long id = delOutboundVO.getId();
            delOutboundBringVerifyDto.setIds(Collections.singletonList(id));
            delOutboundClientService.bringVerify(delOutboundBringVerifyDto);
        } else {
            try {
                // ?????????/??????????????????????????????wms??????????????????????????? ???????????????????????????????????????
                byte[] byteArray = multipartFile.getBytes();
                String encode = Base64.encode(byteArray);
                ShipmentLabelChangeRequestDto shipmentLabelChangeRequestDto = new ShipmentLabelChangeRequestDto();
                shipmentLabelChangeRequestDto.setWarehouseCode(delOutboundVO.getWarehouseCode());
                shipmentLabelChangeRequestDto.setOrderNo(delOutboundVO.getOrderNo());
                shipmentLabelChangeRequestDto.setLabelType("ShipmentLabel");
                shipmentLabelChangeRequestDto.setLabel(encode);
                IHtpOutboundClientService htpOutboundClientService = SpringUtils.getBean(IHtpOutboundClientService.class);
                ResponseVO responseVO = htpOutboundClientService.shipmentLabel(shipmentLabelChangeRequestDto);
                log.info("??????????????????????????????{}-{}", delOutboundVO.getOrderNo(), JSONObject.toJSONString(responseVO));
                if (null == responseVO || null == responseVO.getSuccess()) {
                    throw new CommonException("400", "??????????????????");
                }
                if (!responseVO.getSuccess()) {
                    throw new CommonException("400", StringUtils.nvl(responseVO.getMessage(), "??????????????????2"));
                }
            } catch (IOException e) {
                throw new CommonException("400", "????????????????????????");
            }
            //??????????????????
            delOutboundClientService.updateShipmentLabel(Collections.singletonList(delOutboundVO.getId() + ""));
        }
        return R.ok(i);
    }

    /**
     * ????????????????????????
     *
     * @param orderNo
     * @return
     */
    public boolean verifyOrderSelf(String orderNo, DelOutboundOrderTypeEnum orderType, String... state) {
        TableDataInfo<DelOutboundListVO> page = getDelOutboundListTableDataInfo(orderNo, orderType, state);
        if (null == page || page.getTotal() == 0) {
            return false;
        }
        return true;
    }

    private TableDataInfo<DelOutboundListVO> getDelOutboundListTableDataInfo(String orderNo, DelOutboundOrderTypeEnum orderType, String... state) {
        DelOutboundListQueryDto delOutboundListQueryDto = new DelOutboundListQueryDto();
        delOutboundListQueryDto.setOrderNo(orderNo);
        delOutboundListQueryDto.setOrderType(orderType.getCode());
        delOutboundListQueryDto.setCustomCode(AuthenticationUtil.getSellerCode());
        if (ArrayUtils.isNotEmpty(state)) {
            delOutboundListQueryDto.setState(String.join(",", state));
        } else {
            //???????????? ????????? ?????????
            delOutboundListQueryDto.setState(DelOutboundStateEnum.AUDIT_FAILED.getCode() + "," + DelOutboundStateEnum.REVIEWED.getCode());
        }
        log.info("????????????fegin-???????????????{}", JSON.toJSONString(delOutboundListQueryDto));
        TableDataInfo<DelOutboundListVO> page = this.delOutboundFeignService.page(delOutboundListQueryDto);
        log.info("????????????fegin-???????????????{}", JSON.toJSONString(page));
        return page;
    }

    //@ApiIgnore
    @PreAuthorize("hasAuthority('client')")
    @DeleteMapping("/cancel/batch")
    @ApiOperation(value = "#14 ???????????? - ??????????????????????????????", position = 603)
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "DelOutboundCanceledRequest", required = true)
    public R<Integer> cancelBatch(@RequestBody @Validated DelOutboundCanceledRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("400", "?????????????????????");
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
    @ApiOperation(value = "#15 ???????????? - ??????????????????????????????", position = 700)
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "DelOutboundSelfPickListRequest", required = true)
    public R<List<DelOutboundSelfPickResponse>> selfPick(@RequestBody @Validated(DelOutboundGroup.SelfPick.class) DelOutboundSelfPickListRequest request) {
        List<DelOutboundSelfPickRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("400", "????????????????????????");
        }
        String sellerCode = AuthenticationUtil.getSellerCode();
        requestList.forEach(x -> {
            if (StringUtils.isNotBlank(x.getFile())) {
                byte[] bytes = Base64CheckUtils.checkAndConvert(x.getFile());
                MultipartFile multipartFile = new MockMultipartFile("????????????", ".pdf", "pdf", bytes);
                MultipartFile[] multipartFiles = new MultipartFile[]{multipartFile};
                log.info("remoteAttachmentService.uploadAttachment ?????????{}",JSON.toJSONString(multipartFiles[0].getContentType()));
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

    /**
     * ?????????????????????????????????"."???
     *
     * @param fileName ?????????
     * @return ???????????????
     */
    public static String getFileExtName(String fileName) {
        if (fileName.lastIndexOf(".") != -1) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "";
    }

    //@ApiIgnore
    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/label/selfPick")
    @ApiOperation(value = "#16 ???????????? - ???????????????????????????--???????????????", position = 701)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "orderNo", value = "?????????", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "??????", required = true, allowMultiple = true)
    })
    public R<Integer> labelSelfPick(@RequestParam("orderNo") String orderNo, HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");
        AssertUtil400.isTrue(null != multipartFile, "??????????????????");
        String originalFilename = multipartFile.getOriginalFilename();
        if (null == originalFilename) {
            throw new CommonException("400", "???????????????????????????");
        }
        String fileExtName = getFileExtName(originalFilename);
        if (!("pdf".equals(fileExtName)
                || "jpg".equals(fileExtName)
                || "jpeg".equals(fileExtName)
                || "png".equals(fileExtName))) {
            throw new CommonException("400", "????????????pdf,jpg,jpeg,png??????");
        }
        AssertUtil400.isTrue(StringUtils.isNotBlank(orderNo), "?????????????????????");
        AssertUtil400.isTrue(verifyOrderSelf(orderNo, DelOutboundOrderTypeEnum.SELF_PICK), "????????????????????????????????????????????????");
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
    @ApiOperation(value = "#17 ???????????? - ??????????????????????????????", position = 702)
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "DelOutboundCanceledRequest", required = true)
    public R<Integer> cancelSelfPick(@RequestBody @Validated DelOutboundCanceledRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("400", "?????????????????????");
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
    @ApiOperation(value = "#18 ???????????? - ??????????????????????????????", position = 800)
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "DelOutboundDestroyListRequest", required = true)
    public R<List<DelOutboundDestroyResponse>> destroy(@RequestBody @Validated(DelOutboundGroup.Destroy.class) DelOutboundDestroyListRequest request) {
        List<DelOutboundDestroyRequest> requestList = request.getRequestList();
        if (CollectionUtils.isEmpty(requestList)) {
            throw new CommonException("400", "????????????????????????");
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
    @ApiOperation(value = "#19 ???????????? - ??????????????????????????????", position = 801)
    @ApiImplicitParam(name = "request", value = "????????????", dataType = "DelOutboundCanceledRequest", required = true)
    public R<Integer> cancelDestroy(@RequestBody @Validated DelOutboundCanceledRequest request) {
        List<String> orderNos = request.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("400", "?????????????????????");
        }
        DelOutboundCanceledDto canceledDto = new DelOutboundCanceledDto();
        canceledDto.setOrderNos(orderNos);
        canceledDto.setOrderType(DelOutboundOrderTypeEnum.DESTROY);
        String sellerCode = AuthenticationUtil.getSellerCode();
        canceledDto.setSellerCode(sellerCode);
        return R.ok(this.delOutboundClientService.canceled(canceledDto));
    }

    @PreAuthorize("hasAuthority('client')")
    @GetMapping(value = "/getInfoForThirdParty/{orderNo}")
    @ApiOperation(value = "#20 ???????????? - ?????????????????????????????????", position = 901)
    public R<DelOutboundThirdPartyVO> getInfoForThirdParty(@PathVariable("orderNo") String orderNo) {
        DelOutboundVO vo = new DelOutboundVO();
        String sellerCode = AuthenticationUtil.getSellerCode();
        vo.setSellerCode(sellerCode);
        vo.setOrderNo(orderNo);
        return delOutboundClientService.getInfoForThirdParty(vo);
    }

    @PostMapping(value = "/commonTrackList")
    @ApiOperation(value = "#21 ???????????? - ?????????????????????????????????", position = 902)
    public R<TrackMainDocCommonDto> commonTrackList(@RequestBody @Validated DelTrackRequest request) {
        return trackFeignService.commonTrackList(request.getOrderNos());
    }


    @PreAuthorize("hasAuthority('client')")
    @PostMapping(value = "/updateWeightDelOutbound")
    @ApiOperation(value = "#22 ???????????? - ??????????????????", position = 902)
    public R<Integer> updateWeightDelOutbound(@RequestBody @Validated UpdateWeightDelOutboundDto dto) {
        String sellerCode = AuthenticationUtil.getSellerCode();
        dto.setCustomCode(sellerCode);
        return R.ok(delOutboundClientService.updateWeightDelOutbound(dto));
    }
}
