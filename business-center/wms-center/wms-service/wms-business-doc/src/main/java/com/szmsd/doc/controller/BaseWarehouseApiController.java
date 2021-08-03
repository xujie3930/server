package com.szmsd.doc.controller;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.URLUtil;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.dto.BasWarehouseQueryDTO;
import com.szmsd.bas.vo.BasWarehouseVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.putinstorage.api.feign.InboundReceiptFeignService;
import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Api(tags = {"仓库信息"})
@RestController
@RequestMapping("/api/bas")
public class BaseWarehouseApiController extends BaseController {

    @Resource
    private BasWarehouseClientService basWarehouseClientService;
    @Resource
    private InboundReceiptFeignService inboundReceiptFeignService;

    /**
     * 查询 仓库列表
     *
     * @param queryDTO
     * @return
     */
//    @PreAuthorize("hasAuthority('read')")
    @PostMapping("/warehouse/open/page")
    @ApiOperation(value = "仓库列表-分页查询", notes = "仓库列表 - 分页查询")
    public TableDataInfo<BasWarehouseVO> pagePost(@Validated @RequestBody BasWarehouseQueryDTO queryDTO) {
        return basWarehouseClientService.queryByWarehouseCodes(queryDTO);
    }

    //    @PreAuthorize("hasAuthority('read')")
    @GetMapping("/inbound/receipt/info/{warehouseNo}")
    @ApiImplicitParam(name = "入库单号", value = "warehouseNo", example = "RKCNYWO7210730000009", required = true)
    @ApiOperation(value = "详情", notes = "入库管理 - 详情（包含明细）")
    R<InboundReceiptInfoVO> receiptInfoQuery(@Valid @NotBlank @PathVariable("warehouseNo") String warehouseNo) {
        R<InboundReceiptInfoVO> info = inboundReceiptFeignService.info(warehouseNo);
        Optional.of(info).map(R::getData).flatMap(x -> Optional.of(x).map(InboundReceiptInfoVO::getInboundReceiptDetails)).filter(CollectionUtils::isNotEmpty)
                .ifPresent(inboundReceiptDetailList ->
                        inboundReceiptDetailList.forEach(z -> Optional.ofNullable(z.getEditionImage())
                                .ifPresent(attachmentFileDTO -> attachmentFileDTO.setAttachmentUrl(getBase64Pic(attachmentFileDTO.getAttachmentUrl())))));
        return info;
    }

    public static String getBase64Pic(String picUrl) {
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

    //    @PreAuthorize("hasAuthority('create')")
    @PostMapping("/inbound/receipt/saveOrUpdate/batch")
    @ApiOperation(value = "创建/修改-批量", notes = "批量 入库管理 - 新增/创建")
    R<List<InboundReceiptInfoVO>> saveOrUpdateBatch(@Valid @NotEmpty @RequestBody List<CreateInboundReceiptDTO> createInboundReceiptDTOList) {
        return inboundReceiptFeignService.saveOrUpdateBatch(createInboundReceiptDTOList);
    }

}
