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
    @NotBlank(message = "sku不能为空")
    @ApiModelProperty(value = "sku",required = true)
    private String sku;
    @NotBlank(message = "申报品名不能为空")
    @ApiModelProperty(value = "申报品名",required = true)
    private String skuName;
    @NotNull(message = "申报数量不能为空")
    @ApiModelProperty(value = "申报数量",required = true)
    private Integer declareQty;

    @ApiModelProperty(value = "上架数量",hidden = true)
    private Integer putQty;

    @ApiModelProperty(value = "原产品编码")
    private String originCode;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "对版图片")
    private AttachmentFileDTO editionImage;

    @ApiModelProperty(value = "出库-出库单号作为该单号",hidden = true)
    private String deliveryNo;


}
