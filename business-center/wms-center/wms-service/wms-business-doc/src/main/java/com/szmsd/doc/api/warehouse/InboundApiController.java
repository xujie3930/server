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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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
    @ApiImplicitParam(name = "warehouseNo", value = "入库单号", example = "RKCNYWO7210730000009", required = true)
    @ApiOperation(value = "入库单 - 详情", notes = "根据入库单号查询详情 - 详情（包含明细）")
    R<InboundReceiptInfoResp> receiptInfoQuery(@Valid @NotBlank @PathVariable("warehouseNo") String warehouseNo) {
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
    @ApiOperation(value = "新增/修改-批量入库单", notes = "批量新增/修改入库单信息")
    R<List<InboundReceiptInfoVO>> saveOrUpdateBatch(@Valid @NotEmpty @RequestBody List<CreateInboundReceiptDTO> createInboundReceiptDTOList) {
        return inboundReceiptFeignService.saveOrUpdateBatch(createInboundReceiptDTOList);
    }

    @PreAuthorize("hasAuthority('read')")
    @DeleteMapping("/cancel/{warehouseNo}")
    @ApiImplicitParam(name = "warehouseNo", value = "入库单号", required = true)
    @ApiOperation(value = "取消入库单", notes = "入库管理 - 取消指定的入库单")
    public R<String> cancel(@PathVariable("warehouseNo") String warehouseNo) {
        inboundReceiptFeignService.cancel(warehouseNo);
        return R.ok("success");
    }

    @PreAuthorize("hasAuthority('read')")
    @GetMapping("/getInboundLabel/byOrderNo/{warehouseNo}")
    @ApiImplicitParam(name = "warehouseNo", value = "入库单号", required = true)
    @ApiOperation(value = "获取入库标签-通过单号", notes = "入库管理 - 通过单号获取入库标签 返回 base64")
    public R<String> getInboundLabelByOrderNo(@PathVariable("warehouseNo") String warehouseNo) {
        return R.ok(GoogleBarCodeUtils.generateBarCodeBase64(warehouseNo));
    }

}
