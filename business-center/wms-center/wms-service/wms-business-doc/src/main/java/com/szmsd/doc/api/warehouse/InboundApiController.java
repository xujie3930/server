package com.szmsd.doc.api.warehouse;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.URLUtil;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.BasAttachmentDataDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.domain.BasSellerCertificate;
import com.szmsd.bas.dto.VatQueryDto;
import com.szmsd.bas.dto.WarehouseKvDTO;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.vo.DelOutboundListVO;
import com.szmsd.doc.api.AssertUtil400;
import com.szmsd.doc.api.RUtils;
import com.szmsd.doc.api.warehouse.req.*;
import com.szmsd.doc.api.warehouse.resp.AttachmentFileResp;
import com.szmsd.doc.api.warehouse.resp.InboundReceiptDetailResp;
import com.szmsd.doc.api.warehouse.resp.InboundReceiptInfoResp;
import com.szmsd.doc.api.warehouse.resp.InboundReceiptResp;
import com.szmsd.doc.component.IRemoterApi;
import com.szmsd.doc.config.DocSubConfigData;
import com.szmsd.doc.utils.AuthenticationUtil;
import com.szmsd.doc.utils.GoogleBarCodeUtils;
import com.szmsd.inventory.api.feign.PurchaseFeignService;
import com.szmsd.inventory.domain.dto.TransportWarehousingAddDTO;
import com.szmsd.putinstorage.api.feign.InboundReceiptFeignService;
import com.szmsd.putinstorage.domain.dto.*;
import com.szmsd.putinstorage.domain.vo.InboundReceiptDetailVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;
import com.szmsd.putinstorage.enums.InboundReceiptEnum;
import com.szmsd.putinstorage.enums.SourceTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Api(tags = {"????????????"})
@RestController
@RequestMapping("/api/inboundReceipt")
public class InboundApiController {

    @Resource
    private InboundReceiptFeignService inboundReceiptFeignService;
    @Resource
    private RemoteAttachmentService attachmentFeignService;
    @Resource
    private IRemoterApi iRemoterApi;
    @Resource
    private DocSubConfigData docSubConfigData;
    @Resource
    private PurchaseFeignService purchaseFeignService;

    @PreAuthorize("hasAuthority('client')")
    @GetMapping("/info/{warehouseNo}")
    @ApiImplicitParam(name = "warehouseNo", value = "????????????", example = "RKCNYWO7210730000009", type = "String", required = true)
    @ApiOperation(value = "????????? - ??????", notes = "?????????????????????")
    R<InboundReceiptInfoResp> receiptInfoQuery(@Valid @NotBlank(message = "??????????????????0-30??????") @Size(max = 30) @PathVariable("warehouseNo") String warehouseNo) {
        R<InboundReceiptInfoVO> info = inboundReceiptFeignService.info(warehouseNo);
        AssertUtil400.isTrue(info.getCode() == HttpStatus.SUCCESS && info.getData() != null && info.getData().getCusCode().equals(AuthenticationUtil.getSellerCode()), "??????????????????");
        List<InboundReceiptDetailVO> inboundReceiptDetails = info.getData().getInboundReceiptDetails();

        List<InboundReceiptDetailResp> detailRespList = Optional.ofNullable(inboundReceiptDetails).orElse(new ArrayList<>()).stream().map(x -> {
            InboundReceiptDetailResp inboundReceiptDetailResp = new InboundReceiptDetailResp();
            BeanUtils.copyProperties(x, inboundReceiptDetailResp);
            return inboundReceiptDetailResp;
        }).collect(Collectors.toList());
        InboundReceiptInfoResp inboundReceiptInfoResp = new InboundReceiptInfoResp();
        BeanUtils.copyProperties(info.getData(), inboundReceiptInfoResp);
        inboundReceiptInfoResp.setInboundReceiptDetails(detailRespList);
        Optional.of(inboundReceiptInfoResp).flatMap(x -> Optional.of(x).map(InboundReceiptInfoResp::getInboundReceiptDetails)).filter(CollectionUtils::isNotEmpty)
                .ifPresent(inboundReceiptDetailList ->
                        inboundReceiptDetailList.forEach(z -> Optional.ofNullable(z.getEditionImage())
                                .ifPresent(attachmentFileDTO -> attachmentFileDTO.setAttachmentUrl(getBase64Pic(attachmentFileDTO.getAttachmentUrl())))));

        //???????????????????????????Base64
        BasAttachmentQueryDTO queryDto = new BasAttachmentQueryDTO();
        queryDto.setAttachmentType("??????????????????").setBusinessNo(warehouseNo);
        R<List<BasAttachment>> list = attachmentFeignService.list(queryDto);
        List<BasAttachment> date = getDate(list);
        List<AttachmentFileResp> attachmentFileList = new ArrayList<>();
        date.forEach(x -> {
            AttachmentFileResp inboundReceiptDetailResp = new AttachmentFileResp();
            String base64Pic = getBase64Pic(x.getAttachmentUrl());

            BeanUtils.copyProperties(x, inboundReceiptDetailResp);
            inboundReceiptDetailResp.setAttachment(base64Pic);
            attachmentFileList.add(inboundReceiptDetailResp);
        });
        inboundReceiptInfoResp.setDocumentInformation(attachmentFileList);
        return R.ok(inboundReceiptInfoResp);
    }

    private static <T> T getDate(R<T> info) {
        AssertUtil400.isTrue(info.getCode() == HttpStatus.SUCCESS && info.getData() != null, "????????????!");
        return info.getData();
    }

    private static String getBase64Pic(String picUrl) {
        try (InputStream inputStream = URLUtil.url(picUrl).openConnection().getInputStream();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            int available = inputStream.available();
            byte[] buffer = new byte[available];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            return "data:image/png;base64," + Base64Encoder.encode(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return picUrl;
    }

    /**
     * ??????vat ????????????????????? ???????????????
     *
     * @param remoterApi
     * @return
     */
    public boolean checkVAT(IRemoterApi remoterApi, List<CreateInboundReceiptDTO> addDto) {
        // checkWar
        List<String> warehouseList = addDto.stream().map(InboundReceiptDTO::getWarehouseCode).distinct().collect(Collectors.toList());
        List<String> vatList = addDto.stream().map(InboundReceiptDTO::getVat).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        // ??????????????????
        List<WarehouseKvDTO> warehouseKvDTOS = remoterApi.queryCusInboundWarehouse();
        Map<String, WarehouseKvDTO> warehouseKvDTOMap = warehouseKvDTOS.stream().collect(Collectors.toMap(WarehouseKvDTO::getKey, x -> x, (x1, x2) -> x1));
        BasSellerFeignService basSellerFeignService = remoterApi.getBasSellerFeignService();
        warehouseList.forEach(x -> {
            WarehouseKvDTO warehouseKvDTO = warehouseKvDTOMap.get(x);
            AssertUtil400.isTrue(null != warehouseKvDTO, "???????????????");
        });
        List<String> countryList = warehouseKvDTOMap.values().stream().map(WarehouseKvDTO::getCountry).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(vatList)) return true;
        String joinCountry = String.join(",", countryList);
        VatQueryDto vatQueryDto = new VatQueryDto();
        vatQueryDto.setSellerCode(AuthenticationUtil.getSellerCode());
        vatQueryDto.setCountryCode(joinCountry);
        R<List<BasSellerCertificate>> listR = basSellerFeignService.listVAT(vatQueryDto);
        List<BasSellerCertificate> dataAndException = RUtils.getDataAndException(listR);
        Map<String, List<BasSellerCertificate>> countryMap = dataAndException.stream().collect(Collectors.groupingBy(BasSellerCertificate::getCountryCode));
        Map<Integer, String> errorVatMap = new LinkedHashMap<>();
        AtomicInteger atomicInteger = new AtomicInteger(1);
        addDto.forEach(x -> {
            int andIncrement = atomicInteger.getAndIncrement();
            String vat = x.getVat();
            if (StringUtils.isNotBlank(vat)) {
                WarehouseKvDTO warehouseKvDTO = warehouseKvDTOMap.get(x.getWarehouseCode());
                String country = warehouseKvDTO.getCountry();
                List<BasSellerCertificate> basSellerCertificates = countryMap.get(country);
                List<String> vatListCheck = basSellerCertificates.stream().map(BasSellerCertificate::getVat).collect(Collectors.toList());
                if (!(CollectionUtils.isNotEmpty(basSellerCertificates) && vatListCheck.contains(vat))) {
                    errorVatMap.put(andIncrement, vat);
                }
                // AssertUtil400.isTrue(CollectionUtils.isNotEmpty(basSellerCertificates) && vatListCheck.contains(vat), String.format("VAT[%s]?????????", vat));
            }
        });
        StringBuilder errorVatStr = new StringBuilder();
        errorVatMap.forEach((x, y) -> errorVatStr.append(String.format("???%s???VAT???%s????????????!", x, y)).append(";"));
        AssertUtil400.isTrue(errorVatMap.isEmpty(), errorVatStr.toString());
        return true;
    }


    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/saveOrUpdate/batch")
    @ApiOperation(value = "??????-???????????????", notes = "?????????????????????????????????????????????????????????????????????????????????" +
            "???????????????????????????????????????????????????OMS?????????-????????????-???????????????????????????????????????????????????????????????????????????" +
            "??????????????????????????????WMS??????????????????????????????????????????????????????????????????")
    R<List<String>> saveOrUpdateBatch(@RequestBody @Valid BatchInboundReceiptReq batchInboundReceiptReq) {
        String sellerCode = AuthenticationUtil.getSellerCode();
        batchInboundReceiptReq.getBatchInboundReceiptList().forEach(x -> x.setCusCode(sellerCode));

        List<CreateInboundReceiptReq> createInboundReceiptDTOList = batchInboundReceiptReq.getBatchInboundReceiptList();
        List<CreateInboundReceiptDTO> addDTO = createInboundReceiptDTOList.stream().map(x -> {
            if (CollectionUtils.isNotEmpty(x.getDocumentsFileBase64List())) {
                List<BasAttachmentDataDTO> basAttachmentDataDTOS = iRemoterApi.uploadFile(x.getDocumentsFileBase64List(), AttachmentTypeEnum.INBOUND_RECEIPT_DOCUMENTS);

                List<AttachmentFileDTO> collect1 = basAttachmentDataDTOS.stream().map(file -> {
                    AttachmentFileDTO attachmentFileDTO = new AttachmentFileDTO();
                    BeanUtils.copyProperties(file, attachmentFileDTO);
                    return attachmentFileDTO;
                }).collect(Collectors.toList());
                x.setDocumentsFile(collect1);
            }


            x.calculate();
            x.checkOtherInfo();

            CreateInboundReceiptDTO createInboundReceiptDTO = new CreateInboundReceiptDTO();
            BeanUtils.copyProperties(x, createInboundReceiptDTO);
            createInboundReceiptDTO.setSourceType(SourceTypeEnum.DOC.name());
            List<InboundReceiptDetailReq> inboundReceiptDetails = x.getInboundReceiptDetails();
            if (CollectionUtils.isNotEmpty(inboundReceiptDetails)) {
                List<InboundReceiptDetailDTO> collect = inboundReceiptDetails.stream().map(z -> {
                    InboundReceiptDetailDTO inboundReceiptDetailDTO = new InboundReceiptDetailDTO();
                    BeanUtils.copyProperties(z, inboundReceiptDetailDTO);
                    return inboundReceiptDetailDTO;
                }).collect(Collectors.toList());
                createInboundReceiptDTO.setInboundReceiptDetails(collect);
            }
            return createInboundReceiptDTO;
        }).collect(Collectors.toList());

        addDTO.forEach(x -> {
            String orderType = x.getOrderType();
            //??????????????????????????????
            if (orderType.equals(InboundReceiptEnum.OrderType.COLLECTION.getValue())) {
                String orderNo = x.getOrderNo();
                AssertUtil400.isTrue(StringUtils.isNotBlank(orderNo), "??????????????????????????????");
            }
            // ????????????=????????????????????????????????? 053001
            String deliveryWayCode = x.getDeliveryWayCode();
            DocSubConfigData.SubCode subCode = docSubConfigData.getSubCode();
            if (subCode.getDeliveryWayCode().equals(deliveryWayCode)) {
                AssertUtil400.isTrue(StringUtils.isNotBlank(x.getDeliveryNo()), "??????????????????????????????,??????????????????");
            }
        });
//        List<String> warehouseCodeList = addDTO.stream().map(InboundReceiptDTO::getWarehouseCode).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
//        warehouseCodeList.forEach(x -> {
//            boolean b = iRemoterApi.verifyWarehouse(x);
//            AssertUtil400.isTrue(b, String.format("?????????%s??????????????????", x));
//        });

        List<String> skuList = addDTO.stream().map(CreateInboundReceiptDTO::getInboundReceiptDetails)
                .flatMap(x -> x.stream().map(InboundReceiptDetailDTO::getSku)).distinct().collect(Collectors.toList());
        String cusCode = createInboundReceiptDTOList.get(0).getCusCode();

        String warehouseCode = createInboundReceiptDTOList.get(0).getWarehouseCode();
//        boolean b = iRemoterApi.checkSkuBelong(cusCode, warehouseCode, skuList);
        boolean b = iRemoterApi.checkSkuBelong(cusCode, skuList, addDTO);
        AssertUtil400.isTrue(b, String.format("?????????SKU???%s????????????", skuList));
        //??????vat
        this.checkVAT(iRemoterApi, addDTO);
        List<String> collect = addDTO.stream().filter(x -> "055003".equals(x.getWarehouseMethodCode())).flatMap(z -> z.getInboundReceiptDetails().stream().map(InboundReceiptDetailDTO::getSku)).collect(Collectors.toList());
        iRemoterApi.checkSkuPic(collect, AttachmentTypeEnum.SKU_IMAGE);
        R<List<InboundReceiptInfoVO>> listR = inboundReceiptFeignService.saveOrUpdateBatch(addDTO);
        List<InboundReceiptInfoVO> dataAndException = RUtils.getDataAndException(listR);
        /*List<InboundReceiptInfoResp> result = dataAndException.stream().map(x -> {
            InboundReceiptInfoResp inboundReceiptInfoResp = new InboundReceiptInfoResp();
            BeanUtils.copyProperties(x, inboundReceiptInfoResp);
            inboundReceiptInfoResp.setWarehouseNo(x.getWarehouseNo());
            return inboundReceiptInfoResp;
        }).collect(Collectors.toList());*/
        List<String> warehouseNoList = dataAndException.stream().map(InboundReceiptInfoVO::getWarehouseNo).collect(Collectors.toList());
        return R.ok(warehouseNoList);
    }

    @PreAuthorize("hasAuthority('client')")
    @DeleteMapping("/cancel/{warehouseNo}")
    @ApiImplicitParam(name = "warehouseNo", value = "????????????", required = true)
    @ApiOperation(value = "???????????????", notes = "????????????????????????????????????")
    public R cancel(@PathVariable("warehouseNo") String warehouseNo) {
        R<InboundReceiptInfoVO> info = inboundReceiptFeignService.info(warehouseNo);
        if (info.getCode() != HttpStatus.SUCCESS || info.getData() == null || !info.getData().getCusCode().equals(AuthenticationUtil.getSellerCode())) {
            throw new CommonException("400", "The receipt order does not exist");
        }

        R cancel = null;
        try {
            cancel = inboundReceiptFeignService.cancel(warehouseNo);
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed(e.getMessage());
        }
        return cancel;
    }

    @PreAuthorize("hasAuthority('client')")
    @GetMapping("/getInboundLabel/byOrderNo/{warehouseNo}")
    @ApiImplicitParam(name = "warehouseNo", value = "????????????", required = true)
    @ApiOperation(value = "??????????????????-????????????", notes = "???????????????????????????????????????????????????????????????????????????Base64")
    public R<String> getInboundLabelByOrderNo(@Valid @NotBlank @Size(max = 30) @PathVariable("warehouseNo") String warehouseNo) {
        R<InboundReceiptInfoVO> info = inboundReceiptFeignService.info(warehouseNo);
        AssertUtil400.isTrue(info.getCode() == HttpStatus.SUCCESS && info.getData() != null && info.getData().getCusCode().equals(AuthenticationUtil.getSellerCode()), "??????????????????");
        return R.ok(GoogleBarCodeUtils.generateBarCodeBase64(warehouseNo));
    }

    public void checkCategoryCode(String categoryCode) {
        List<String> categoryCodeList = new ArrayList<>();
        categoryCodeList.add("056001");
        categoryCodeList.add("056002");
        boolean containsCate = categoryCodeList.contains(categoryCode);
        AssertUtil400.isTrue(containsCate, "???????????????");
    }

    @Resource
    private DelOutboundFeignService delOutboundFeignService;

    @PreAuthorize("hasAuthority('client')")
    @PostMapping(value = "transport/warehousing")
    @ApiOperation(value = "????????????-??????")
    public R transportWarehousingSubmit(@Validated @RequestBody TransportWarehousingAddRep transportWarehousingAddRep) {
        long count = transportWarehousingAddRep.getTransferNoList().stream().filter(StringUtils::isNotBlank).count();
        AssertUtil400.isTrue(count > 0, "????????????????????????????????????");
        String categoryCode = transportWarehousingAddRep.getWarehouseCategoryCode();
        this.checkCategoryCode(categoryCode);


        // ????????????
        String deliveryWay = transportWarehousingAddRep.getDeliveryWay();
        checkDeliveryWayCode(deliveryWay);

        //?????????????????????????????? ?????? ??????????????? ?????? ???????????????
        DelOutboundListQueryDto delOutboundListQueryDto = new DelOutboundListQueryDto();
        delOutboundListQueryDto.setCustomCode(AuthenticationUtil.getSellerCode());
        delOutboundListQueryDto.setOrderNo(String.join(",", transportWarehousingAddRep.getTransferNoList()));
        delOutboundListQueryDto.setOrderType(DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode());
        TableDataInfo<DelOutboundListVO> info = delOutboundFeignService.page(delOutboundListQueryDto);
        AssertUtil400.isTrue(info.getCode() == HttpStatus.SUCCESS && info.getRows() != null, "??????????????????");
        List<DelOutboundListVO> rows = info.getRows();
        Map<String, List<DelOutboundListVO>> collect = rows.stream().collect(Collectors.groupingBy(DelOutboundListVO::getWarehouseCode));
        AssertUtil400.isTrue(MapUtils.isNotEmpty(collect), "??????????????????");
        AssertUtil400.isTrue(collect.keySet().size() <= 1, "????????????????????????????????????");
        List<String> isNotLabelBoxList = rows.stream().filter(x -> !x.getIsPrint()).map(DelOutboundListVO::getOrderNo).collect(Collectors.toList());
        AssertUtil400.isTrue(CollectionUtils.isEmpty(isNotLabelBoxList), String.format("????????????%s??????????????????", isNotLabelBoxList));
        // ????????????
        List<DelOutboundListVO> result = rows.stream().filter(x -> !x.getState().equals(DelOutboundStateEnum.DELIVERED.getCode())).collect(Collectors.toList());
//        List<DelOutboundListVO> result = rows.stream().filter(x -> StringUtils.isNotBlank(x.getPurchaseNo())).collect(Collectors.toList());
        AssertUtil400.isTrue(CollectionUtils.isEmpty(result), String.format("???????????????%s?????????", result.stream().map(DelOutboundListVO::getOrderNo).collect(Collectors.toList())));
//        //check vat
        // ??????????????????
        List<WarehouseKvDTO> warehouseKvDTOS = iRemoterApi.queryCusInboundWarehouse();
        Map<String, WarehouseKvDTO> warehouseKvDTOMap = warehouseKvDTOS.stream().collect(Collectors.toMap(WarehouseKvDTO::getKey, x -> x, (x1, x2) -> x1));
        BasSellerFeignService basSellerFeignService = iRemoterApi.getBasSellerFeignService();
        String warehouseCode = rows.get(0).getWarehouseCode();
        WarehouseKvDTO warehouseKvDTO = warehouseKvDTOMap.get(warehouseCode);
        AssertUtil400.isTrue(null != warehouseKvDTO, "???????????????");
        String vat = transportWarehousingAddRep.getVat();
        if (StringUtils.isNotBlank(vat)) {
            List<String> countryList = warehouseKvDTOMap.values().stream().map(WarehouseKvDTO::getCountry).filter(StringUtils::isNotBlank).collect(Collectors.toList());

            String joinCountry = String.join(",", countryList);
            VatQueryDto vatQueryDto = new VatQueryDto();
            vatQueryDto.setSellerCode(AuthenticationUtil.getSellerCode());
            vatQueryDto.setCountryCode(joinCountry);
            R<List<BasSellerCertificate>> listR = basSellerFeignService.listVAT(vatQueryDto);
            List<BasSellerCertificate> dataAndException = RUtils.getDataAndException(listR);
            Map<String, List<BasSellerCertificate>> countryMap = dataAndException.stream().collect(Collectors.groupingBy(BasSellerCertificate::getCountryCode));

            String country = warehouseKvDTO.getCountry();
            List<BasSellerCertificate> basSellerCertificates = countryMap.get(country);
            List<String> vatListCheck = basSellerCertificates.stream().map(BasSellerCertificate::getVat).collect(Collectors.toList());
            AssertUtil400.isTrue(CollectionUtils.isNotEmpty(basSellerCertificates) && vatListCheck.contains(vat), String.format("VAT[%s]?????????", vat));

        }

        TransportWarehousingAddDTO transportWarehousingAddDTO = new TransportWarehousingAddDTO();
        BeanUtils.copyProperties(transportWarehousingAddRep, transportWarehousingAddDTO);
        transportWarehousingAddDTO.setCustomCode(AuthenticationUtil.getSellerCode());
        transportWarehousingAddDTO.setWarehouseCode(warehouseCode);
        List<String> idList = rows.stream().map(DelOutboundListVO::getId).map(String::valueOf).collect(Collectors.toList());
        transportWarehousingAddDTO.setIdList(idList);
        R info2 = purchaseFeignService.transportWarehousingSubmit(transportWarehousingAddDTO);
        AssertUtil400.isTrue(info2.getCode() == HttpStatus.SUCCESS && info2.getData() != null, "??????????????????");
        return R.ok();
    }

    private void checkDeliveryWayCode(String deliveryWayCode) {
        List<String> deliveryWayCodeList = new ArrayList<String>();
        deliveryWayCodeList.add("053001");
        deliveryWayCodeList.add("053002");
        deliveryWayCodeList.add("053003");
        boolean containsDeliveryWayCode = deliveryWayCodeList.contains(deliveryWayCode);
        AssertUtil400.isTrue(containsDeliveryWayCode, "?????????????????????");
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/page")
    @ApiOperation(value = "???????????????-????????????", notes = "???????????? - ????????????")
    public TableDataInfo<InboundReceiptResp> postPage(@RequestBody InboundReceiptQueryReq queryDTO) {
        InboundReceiptQueryDTO inboundReceiptQueryDTO = new InboundReceiptQueryDTO();
        BeanUtils.copyProperties(queryDTO, inboundReceiptQueryDTO);
        inboundReceiptQueryDTO.setCusCode(AuthenticationUtil.getSellerCode());
        TableDataInfo<InboundReceiptVO> inboundReceiptVOTableDataInfo = inboundReceiptFeignService.postPage(inboundReceiptQueryDTO);
        List<InboundReceiptVO> rows = inboundReceiptVOTableDataInfo.getRows();
        TableDataInfo<InboundReceiptResp> inboundReceiptRespTableDataInfo = new TableDataInfo<>();
        BeanUtils.copyProperties(inboundReceiptRespTableDataInfo, inboundReceiptRespTableDataInfo);
        List<InboundReceiptResp> collect = rows.stream().map(x -> {
            InboundReceiptResp inboundReceiptResp = new InboundReceiptResp();
            BeanUtils.copyProperties(x, inboundReceiptResp);
            return inboundReceiptResp;
        }).collect(Collectors.toList());
        inboundReceiptRespTableDataInfo.setCode(200);
        inboundReceiptRespTableDataInfo.setTotal(inboundReceiptVOTableDataInfo.getTotal());
        inboundReceiptRespTableDataInfo.setRows(collect);
        return inboundReceiptRespTableDataInfo;
    }

    @Resource
    private HttpServletRequest httpServletRequest;

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/updateTrackingNo")
    @ApiOperation(value = "????????????????????????", notes = "???????????????????????? ?????????/??????????????????????????????")
    public R<Integer> updateTrackingNo(@Validated @RequestBody UpdateTrackingNoRequest updateTrackingNoRequest) {
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            Enumeration<String> values = httpServletRequest.getHeaders(name);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                log.info(name + "---" + value);
            }
        }
        String sellerCode = AuthenticationUtil.getSellerCode();
        AssertUtil400.isTrue(StringUtils.isNotBlank(sellerCode), "????????????????????????");
        updateTrackingNoRequest.setSellerCode(sellerCode);
        return inboundReceiptFeignService.updateTracking(updateTrackingNoRequest);
    }
}
