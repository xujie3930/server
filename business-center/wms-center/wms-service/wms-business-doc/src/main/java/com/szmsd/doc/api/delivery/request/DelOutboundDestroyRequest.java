package com.szmsd.doc.api.delivery.request;

import com.szmsd.doc.api.delivery.request.group.DelOutboundGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "DelOutboundDestroyRequest", description = "DelOutboundDestroyRequest对象")
public class DelOutboundDestroyRequest implements Serializable {

    @NotBlank(message = "客户编码不能为空", groups = {DelOutboundGroup.Destroy.class})
    @ApiModelProperty(value = "客户编码", required = true, dataType = "String")
    private String sellerCode;

    @NotBlank(message = "仓库编码不能为空", groups = {DelOutboundGroup.Destroy.class})
    @ApiModelProperty(value = "仓库编码", required = true, dataType = "String")
    private String warehouseCode;

    @ApiModelProperty(value = "是否优先发货", dataType = "Boolean", position = 1, example = "false")
    private Boolean isFirst;

    @Size(max = 50, message = "增值税号不能超过50个字符")
    @ApiModelProperty(value = "增值税号", dataType = "String", position = 2, example = "F00X")
    private String ioss;

    @Size(max = 500, message = "备注不能超过500个字符")
    @ApiModelProperty(value = "备注", dataType = "String", position = 3, example = "")
    private String remark;

    @Valid
    @ApiModelProperty(value = "明细信息", dataType = "DelOutboundDetailRequest", position = 4)
    private List<DelOutboundDetailRequest> details;

}
