package com.szmsd.putinstorage.domain.dto;

import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "InboundReceiptDTO", description = "入库参数")
public class InboundReceiptDTO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "入库单号", hidden = true)
    private String warehouseNo;

    @ApiModelProperty(value = "采购单")
    private String orderNo;

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "入库单类型：普通入库（OMS用）：Normal, 转运入库（OMS用）：Transfer, 采购入库（OMS用）：Purchase, 上架入库（Yewu用）：Putaway, 点数入库（Yewu用）：Counting")
    private String orderType;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "入库方式编码")
    private String warehouseMethodCode;

    @ApiModelProperty(value = "类别编码")
    private String warehouseCategoryCode;

    @ApiModelProperty(value = "VAT")
    private String vat;

    @ApiModelProperty(value = "送货方式编码")
    private String deliveryWayCode;

    @ApiModelProperty(value = "送货单号")
    private String deliveryNo;

    @ApiModelProperty(value = "合计申报数量")
    private Integer totalDeclareQty;

    @ApiModelProperty(value = "合计上架数量")
    private Integer totalPutQty;

    @ApiModelProperty(value = "产品货源地编码")
    private String goodsSourceCode;

    @ApiModelProperty(value = "挂号")
    private String trackingNumber;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "单证信息文件")
    private AttachmentDataDTO documentsFile;

    @ApiModelProperty(value = "状态0已取消，1待提审，2待收货，3仓库处理中，4已入库")
    private String status;

}
