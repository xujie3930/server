package com.szmsd.doc.api.warehouse.req;

import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class InboundReceiptDetailReq {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "入库单号", hidden = true)
    private String warehouseNo;
    @NotBlank(message = "sku不能为空")
    @ApiModelProperty(value = "sku", example = "SCNYWO7000234", required = true)
    private String sku;
    @NotBlank(message = "申报品名不能为空")
    @ApiModelProperty(value = "申报品名", example = "English", required = true)
    private String skuName;
    @Min(value = 1, message = "申报数量不能小于1")
    @NotNull(message = "申报数量不能为空")
    @ApiModelProperty(value = "申报数量", example = "1", required = true)
    private Integer declareQty;

    @ApiModelProperty(value = "上架数量", hidden = true)
    private Integer putQty;

    @ApiModelProperty(value = "原产品编码", example = "2")
    private String originCode;

    @ApiModelProperty(value = "备注", example = "备注")
    private String remark;

    @ApiModelProperty(value = "对版图片")
    private AttachmentFileDTO editionImage;

    @ApiModelProperty(value = "出库-出库单号作为该单号", hidden = true)
    private String deliveryNo;


}
