package com.szmsd.doc.api.delivery.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(description = "包装信息 - 选则按要求装箱需要填写")
public class DelOutboundBatchPackingRequest implements Serializable {
//    @NotNull(message = "数量不能为空")
    @Min(value = 0,message = "数量异常")
    @ApiModelProperty(value = "包裹数量",hidden = true)
    private Long qty;

    @ApiModelProperty(value = "箱号",hidden = true)
    private String packingNo;

    @ApiModelProperty(value = "长 CM",hidden = true)
    private Double length;

    @ApiModelProperty(value = "宽 CM",hidden = true)
    private Double width;

    @ApiModelProperty(value = "高 CM",hidden = true)
    private Double height;

    @ApiModelProperty(value = "重量 g",hidden = true)
    private Double weight;

    @ApiModelProperty(value = "包材类型",hidden = true)
    private String packingMaterial;

    @Valid
    @NotEmpty(message = "明细不能为空")
    @ApiModelProperty(value = "明细")
    private List<DelOutboundBatchPackingDetailRequest> details;
}
