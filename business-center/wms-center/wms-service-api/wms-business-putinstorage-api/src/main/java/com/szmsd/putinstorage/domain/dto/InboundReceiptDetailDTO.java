package com.szmsd.putinstorage.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class InboundReceiptDetailDTO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "入库单号", hidden = true)
    private String warehouseNo;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "申报品名")
    private String skuName;

    @ApiModelProperty(value = "申报数量")
    private Integer declareQty;

    @ApiModelProperty(value = "上架数量")
    private Integer putQty;

    @ApiModelProperty(value = "原产品编码")
    private String originCode;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "对版图片")
    private AttachmentFileDTO editionImage;

}
