package com.szmsd.doc.api.warehouse;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.URLUtil;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.doc.api.warehouse.resp.AttachmentFileResp;
import com.szmsd.doc.api.warehouse.resp.InboundReceiptInfoResp;
import com.szmsd.doc.utils.GoogleBarCodeUtils;
import com.szmsd.putinstorage.api.feign.InboundReceiptFeignService;
import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Api(tags = {"入库信息"})
@RestController
@RequestMapping("/api/inboundReceipt")
public class InboundApiController extends BaseController {

    @Resource
    private InboundReceiptFeignService inboundReceiptFeignService;
    @Resource
    private RemoteAttachmentService attachmentFeignService;

    @PreAuthorize("hasAuthority('read')")
    @GetMapping("/info/{warehouseNo}")
    @ApiImplicitParam(name = "warehouseNo", value = "入库单号", example = "RKCNYWO7210730000009", type = "String", required = true)
    @ApiOperation(value = "入库单 - 详情", notes = "查看入库单详情")
    R<InboundReceiptInfoResp> receiptInfoQuery(@Validated @NotBlank @Size(max = 30) @PathVariable("warehouseNo") String warehouseNo) {
        R<InboundReceiptInfoVO> info = inboundReceiptFeignService.info(warehouseNo);
        AssertUtil.isTrue(info.getCode() == HttpStatus.SUCCESS && info.getData() != null, "获取详情异常");
        InboundReceiptInfoResp inboundReceiptInfoResp = new InboundReceiptInfoResp();
        BeanUtils.copyProperties(info.getData(), inboundReceiptInfoResp);
        Optional.of(inboundReceiptInfoResp).flatMap(x -> Optional.of(x).map(InboundReceiptInfoResp::getInboundReceiptDetails)).filter(CollectionUtils::isNotEmpty)
                .ifPresent(inboundReceiptDetailList ->
                        inboundReceiptDetailList.forEach(z -> Optional.ofNullable(z.getEditionImage())
                                .ifPresent(attachmentFileDTO -> attachmentFileDTO.setAttachmentUrl(getBase64Pic(attachmentFileDTO.getAttachmentUrl())))));

        //获取单证信息转换成Base64
        BasAttachmentQueryDTO queryDto = new BasAttachmentQueryDTO();
        queryDto.setAttachmentType("单证信息文件").setBusinessNo(warehouseNo);
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
        AssertUtil.isTrue(info.getCode() == HttpStatus.SUCCESS && info.getData() != null, "获取失败!");
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

    @PreAuthorize("hasAuthority('read')")
    @PostMapping("/saveOrUpdate/batch")
    @ApiOperation(value = "新增/修改-批量入库单", notes = "新建入库单，入库单提交后，视入库仓库是否需要人工审核，" +
            "如果需要管理人员人工审核，则需进入OMS客户端-仓储服务-入库管理，再次提交入库申请。如仓库设置为自动审核，" +
            "则入库申请单直接推送WMS，并根据相应规则计算费用。支持批量导入入库单")
    R<List<InboundReceiptInfoVO>> saveOrUpdateBatch(@Validated @NotEmpty @RequestBody List<CreateInboundReceiptDTO> createInboundReceiptDTOList) {
        return inboundReceiptFeignService.saveOrUpdateBatch(createInboundReceiptDTOList);
    }

    @PreAuthorize("hasAuthority('read')")
    @DeleteMapping("/cancel/{warehouseNo}")
    @ApiImplicitParam(name = "warehouseNo", value = "入库单号", required = true)
    @ApiOperation(value = "取消入库单", notes = "取消仓库还未处理的入库单")
    public R<String> cancel(@PathVariable("warehouseNo") String warehouseNo) {
        inboundReceiptFeignService.cancel(warehouseNo);
        return R.ok("success");
    }

    @PreAuthorize("hasAuthority('read')")
    @GetMapping("/getInboundLabel/byOrderNo/{warehouseNo}")
    @ApiImplicitParam(name = "warehouseNo", value = "入库单号", required = true)
    @ApiOperation(value = "获取入库标签-通过单号", notes = "根据入库单号，生成标签条形码，返回的为条形码图片的Base64")
    public R<String> getInboundLabelByOrderNo(@PathVariable("warehouseNo") String warehouseNo) {
        return R.ok(GoogleBarCodeUtils.generateBarCodeBase64(warehouseNo));
    }

}