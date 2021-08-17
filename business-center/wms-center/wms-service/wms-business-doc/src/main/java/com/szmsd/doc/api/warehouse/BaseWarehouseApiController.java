package com.szmsd.doc.api.warehouse;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.URLUtil;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.dto.BasWarehouseQueryDTO;
import com.szmsd.bas.vo.BasWarehouseVO;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.doc.api.warehouse.req.InventoryAvailableQueryReq;
import com.szmsd.doc.api.warehouse.resp.InboundReceiptInfoResp;
import com.szmsd.doc.api.warehouse.resp.InventoryAvailableListResp;
import com.szmsd.doc.api.warehouse.resp.SkuInventoryAgeResp;
import com.szmsd.doc.utils.GoogleBarCodeUtils;
import com.szmsd.inventory.api.service.InventoryFeignClientService;
import com.szmsd.inventory.domain.dto.InventoryAvailableQueryDto;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import com.szmsd.inventory.domain.vo.SkuInventoryAgeVo;
import com.szmsd.putinstorage.api.feign.InboundReceiptFeignService;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
// import jdk.jfr.internal.tool.Main;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = {"仓库信息"})
@RestController
@RequestMapping("/api/bas")
public class BaseWarehouseApiController extends BaseController {

    @Resource
    private BasWarehouseClientService basWarehouseClientService;
    @Resource
    private InboundReceiptFeignService inboundReceiptFeignService;
    @Resource
    private RemoteAttachmentService attachmentFeignService;
    @Resource
    private InventoryFeignClientService inventoryFeignService;


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
        List<AttachmentFileDTO> attachmentFileList = new ArrayList<>();
        date.forEach(x -> {
            AttachmentFileDTO inboundReceiptDetailResp = new AttachmentFileDTO();
            String base64Pic = getBase64Pic(x.getAttachmentUrl());

            BeanUtils.copyProperties(x, inboundReceiptDetailResp);
            inboundReceiptDetailResp.setAttachmentUrl(base64Pic);
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

    //    @PreAuthorize("hasAuthority('create')")
    @PostMapping("/inbound/receipt/saveOrUpdate/batch")
    @ApiOperation(value = "新增/修改-批量入库单", notes = "批量新增/修改入库单信息")
    R<List<InboundReceiptInfoVO>> saveOrUpdateBatch(@Valid @NotEmpty @RequestBody List<CreateInboundReceiptDTO> createInboundReceiptDTOList) {
        return inboundReceiptFeignService.saveOrUpdateBatch(createInboundReceiptDTOList);
    }

    @DeleteMapping("/receipt/cancel/{warehouseNo}")
    @ApiImplicitParam(name = "warehouseNo", value = "入库单号", required = true)
    @ApiOperation(value = "取消入库单", notes = "入库管理 - 取消指定的入库单")
    public R cancel(@PathVariable("warehouseNo") String warehouseNo) {
        inboundReceiptFeignService.cancel(warehouseNo);
        return R.ok();
    }

    @GetMapping("/inbound/getInboundLabel/byOrderNo/{warehouseNo}")
    @ApiImplicitParam(name = "warehouseNo", value = "入库单号", required = true)
    @ApiOperation(value = "获取入库标签-通过单号", notes = "入库管理 - 通过单号获取入库标签 返回 base64")
    public R<String> getInboundLabelByOrderNo(@PathVariable("warehouseNo") String warehouseNo) {
        return R.ok(GoogleBarCodeUtils.generateBarCodeBase64(warehouseNo));
    }

    //    @PreAuthorize("hasAuthority('read')")
    @PostMapping("/inbound/queryAvailableList")
    @ApiOperation(value = "查询可用库存-根据仓库编码，SKU - 不分页")
    public R<List<InventoryAvailableListResp>> queryAvailableList(@RequestBody InventoryAvailableQueryReq queryDTO) {
        List<InventoryAvailableListVO> inventoryAvailableListVOS = inventoryFeignService.queryAvailableList(queryDTO.convertThis());

        List<InventoryAvailableListResp> returnList =inventoryAvailableListVOS
                .stream().filter(Objects::nonNull).map(InventoryAvailableListResp::convertThis).collect(Collectors.toList());

        return R.ok(returnList);
    }

    //    @PreAuthorize("hasAuthority('read')")
    @PostMapping("/inbound/queryOnlyAvailable")
    @ApiOperation(value = "根据仓库编码，SKU查询可用库存 - 单条")
    public R<InventoryAvailableListResp> queryOnlyAvailable(@RequestBody InventoryAvailableQueryReq queryDto) {

        InventoryAvailableListVO inventoryAvailableListVO = inventoryFeignService.queryOnlyAvailable(queryDto.convertThis());

        return R.ok(InventoryAvailableListResp.convertThis(inventoryAvailableListVO));
    }

    //@PreAuthorize("hasAuthority('read')")
    @GetMapping("/queryInventoryAge/weeks/bySku/{warehouseCode}/{sku}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "warehouseCode", value = "仓库code", example = "NJ"),
            @ApiImplicitParam(name = "sku", value = "sku", example = "CN20210601006"),
    })
    @ApiOperation(value = "库龄-查询sku的库龄", notes = "查询sku的库龄-周")
    public R<List<SkuInventoryAgeResp>> queryInventoryAgeBySku(@PathVariable("warehouseCode") String warehouseCode, @PathVariable("sku") String sku) {
        List<SkuInventoryAgeVo> skuInventoryAgeVos = inventoryFeignService.queryInventoryAgeBySku(warehouseCode, sku);
        return R.ok(SkuInventoryAgeResp.convert(skuInventoryAgeVos,warehouseCode,sku));
    }
}
