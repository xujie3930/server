package com.szmsd.doc.api.delivery.response;

import com.szmsd.common.core.annotation.Excel;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "InboundReceiptDetailVO", description = "InboundReceiptDetailVO入库明细视图")
public class InboundReceiptDetailResp {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "入库单号")
    private String warehouseNo;

    @ApiModelProperty(value = "sku")
    @Excel(name = "SKU")
    private String sku;

    @ApiModelProperty(value = "申报品名")
    private String skuName;

    @ApiModelProperty(value = "申报数量")
    @Excel(name = "申报数量")
    private Integer declareQty;

    @ApiModelProperty(value = "上架数量")
    private Integer putQty;

    @ApiModelProperty(value = "原产品编码")
    @Excel(name = "原产品编码")
    private String originCode;

    @ApiModelProperty(value = "采购-运单号/出库-出库单号作为该单号")
    private String deliveryNo;

    @ApiModelProperty(value = "对版图片")
    private AttachmentFileDTO editionImage;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

}
