package com.szmsd.putinstorage.domain.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.utils.StringToolkit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Validated
@Data
@Accessors(chain = true)
@ApiModel(value = "InboundReceiptDTO", description = "入库参数")
public class InboundReceiptDTO {

    @ApiModelProperty(value = "主键ID")
    private Long id;
    @Size(max = 30, message = "入库单号仅支持0-30字符")
    @ApiModelProperty(value = "入库单号 (0-30]", hidden = true)
    private String warehouseNo;
    @Size(max = 30, message = "采购单仅支持0-30字符")
    @ApiModelProperty(value = "采购单 (0-30] 入库方式为集运入库 采购单号必填")
    private String orderNo;
    @Size(max = 30, message = "客户编码仅支持 0-30字符")
    @ApiModelProperty(value = "客户编码 (0-30]")
    private String cusCode;
    @Size(max = 30, message = "入库方式仅支持0-30字符")
    @ApiModelProperty(value = "普通入库（OMS用）：Normal" +
            "集运入库（OMS用）：Collection" +
            "包裹转运入库（OMS用）：PackageTransfer" +
            "新SKU入库（OMS用）：NewSku" +
            "上架入库（Yewu用）：Putaway" +
            "点数入库（Yewu用）：Counting  (0-30]")
    private String orderType;
    @NotBlank(message = "目的仓库编码不能为空")
    @Size(max = 30, message = "目的仓库编码仅支持0-30字符")
    @ApiModelProperty(value = "目的仓库编码 (0-30]", required = true)
    private String warehouseCode;
    @NotBlank(message = "入库方式编码不能为空")
    @Size(max = 30, message = "入库方式编码仅支持0-30字符")
    @ApiModelProperty(value = "入库方式编码 (0-30]", required = true)
    private String warehouseMethodCode;
    @NotBlank(message = "类别编码不能为空")
    @Size(max = 30, message = "类别编码仅支持0-30字符")
    @ApiModelProperty(value = "类别编码 (0-30]",allowableValues = "SKU,包材",required = true)
    private String warehouseCategoryCode;
    @Size(max = 30, message = "VAT 仅支持 0-30 字符")
    @ApiModelProperty(value = "VAT")
    private String vat;
    @NotBlank(message = "送货方式编码不能为空")
    @Size(max = 30, message = "送货方式编码仅支持0-30字符")
    @ApiModelProperty(value = "送货方式编码 (0-30]", required = true)
    private String deliveryWayCode;
//    @Size(max = 200, message = "送货单号仅支持0-30字符")
    @ApiModelProperty(value = "送货单号 可支持多个 送货方式为快递到仓时,送货单号必填")
    private String deliveryNo;

    public InboundReceiptDTO setDeliveryNo(String deliveryNo) {
        this.deliveryNo = deliveryNo;
        this.deliveryNoList =  Optional.ofNullable(StringToolkit.getCodeByArray(deliveryNo)).orElse(new ArrayList<>()).stream().distinct().collect(Collectors.toList());
        return this;
    }

    @ApiModelProperty(value = "送货单号-多个",hidden = true)
    private List<String> deliveryNoList;
    @NotNull(message = "合计申报数量不能为空")
    @Min(value = 0, message = "合计申报数量不能小于0")
    @ApiModelProperty(value = "合计申报数量", required = true)
    private Integer totalDeclareQty;
    @Min(value = 0, message = "合计上架数量不能小于0")
    @ApiModelProperty(value = "合计上架数量")
    private Integer totalPutQty;
    @Size(max = 30, message = "产品货源地编码仅支持0-30字符")
    @ApiModelProperty(value = "产品货源地编码 (0-30]")
    private String goodsSourceCode;
    @Size(max = 200, message = "挂号长度仅支持0-200字符")
    @ApiModelProperty(value = "挂号 (0-200]")
    private String trackingNumber;
    @Size(max = 500, message = "备注长度仅支持0-500字符")
    @ApiModelProperty(value = "备注 (0-500]")
    private String remark;

    @ApiModelProperty(value = "单证信息文件")
    private List<AttachmentFileDTO> documentsFile;

    @ApiModelProperty(value = "状态0已取消，1初始，2已提审，3审核通过，-3审核失败，4处理中，5已完成")
    private String status;
    @ApiModelProperty(value = "数据来源")
    private String sourceType;
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
