package com.szmsd.putinstorage.domain.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "InboundReceiptDTO", description = "入库参数")
public class InboundReceiptDTO {

    @ApiModelProperty(value = "主键ID")
    private Long id;
    @Size(max = 30)
    @ApiModelProperty(value = "入库单号", hidden = true)
    private String warehouseNo;
    @Size(max = 30)
    @ApiModelProperty(value = "采购单")
    private String orderNo;
    @Size(max = 30)
    @ApiModelProperty(value = "客户编码")
    private String cusCode;
    @Size(max = 30)
    @ApiModelProperty(value = "普通入库（OMS用）：Normal" +
            "集运入库（OMS用）：Collection" +
            "包裹转运入库（OMS用）：PackageTransfer" +
            "新SKU入库（OMS用）：NewSku" +
            "上架入库（Yewu用）：Putaway" +
            "点数入库（Yewu用）：Counting")
    private String orderType;
    @NotBlank
    @Size(max = 30)
    @ApiModelProperty(value = "目的仓库编码",required = true)
    private String warehouseCode;
    @NotBlank
    @Size(max = 30)
    @ApiModelProperty(value = "入库方式编码",required = true)
    private String warehouseMethodCode;
    @NotBlank
    @Size(max = 30)
    @ApiModelProperty(value = "类别编码",required = true)
    private String warehouseCategoryCode;
    @Size(max = 30)
    @ApiModelProperty(value = "VAT")
    private String vat;
    @NotBlank
    @Size(max = 30)
    @ApiModelProperty(value = "送货方式编码",required = true)
    private String deliveryWayCode;
    @Size(max = 30)
    @ApiModelProperty(value = "送货单号")
    private String deliveryNo;
    @NotNull
    @Min(0)
    @ApiModelProperty(value = "合计申报数量",required = true)
    private Integer totalDeclareQty;
    @Min(0)
    @ApiModelProperty(value = "合计上架数量")
    private Integer totalPutQty;
    @Size(max = 30)
    @ApiModelProperty(value = "产品货源地编码")
    private String goodsSourceCode;
    @Size(max = 30)
    @ApiModelProperty(value = "挂号")
    private String trackingNumber;
    @Size(max = 500)
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "单证信息文件")
    private List<AttachmentFileDTO> documentsFile;

    @ApiModelProperty(value = "状态0已取消，1初始，2已提审，3审核通过，-3审核失败，4处理中，5已完成")
    private String status;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
