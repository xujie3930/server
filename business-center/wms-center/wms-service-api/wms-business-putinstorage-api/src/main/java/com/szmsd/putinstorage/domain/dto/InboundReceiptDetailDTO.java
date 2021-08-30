package com.szmsd.putinstorage.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class InboundReceiptDetailDTO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "入库单号", hidden = true)
    private String warehouseNo;
    @NotBlank
    @ApiModelProperty(value = "sku",required = true)
    private String sku;
    @NotBlank
    @ApiModelProperty(value = "申报品名",required = true)
    private String skuName;
    @NotNull
    @ApiModelProperty(value = "申报数量",required = true)
    private Integer declareQty;

    @ApiModelProperty(value = "上架数量")
    private Integer putQty;

    @ApiModelProperty(value = "原产品编码")
    private String originCode;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "对版图片")
    private AttachmentFileDTO editionImage;

    @ApiModelProperty(value = "采购-运单号/出库-出库单号作为该单号")
    private String deliveryNo;


}
