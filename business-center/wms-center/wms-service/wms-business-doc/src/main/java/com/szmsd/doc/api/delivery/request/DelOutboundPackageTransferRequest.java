package com.szmsd.doc.api.delivery.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 转运出库
 *
 * @author zhangyuyuan
 * @date 2021-08-03 9:46
 */
@Data
@ApiModel(value = "DelOutboundPackageTransferRequest", description = "DelOutboundPackageTransferRequest对象")
public class DelOutboundPackageTransferRequest implements Serializable {

    @NotNull(message = "重量不能为空")
    @Size(message = "重量必须介于0~2147483647之间")
    @ApiModelProperty(value = "重量 g", required = true, dataType = "Double", position = 1, example = "10")
    private Double weight;

    @NotNull(message = "长不能为空")
    @Size(message = "长必须介于0~2147483647之间")
    @ApiModelProperty(value = "长 CM", required = true, dataType = "Double", position = 2, example = "10")
    private Double length;

    @NotNull(message = "宽不能为空")
    @Size(message = "宽必须介于0~2147483647之间")
    @ApiModelProperty(value = "宽 CM", required = true, dataType = "Double", position = 3, example = "10")
    private Double width;

    @NotNull(message = "高不能为空")
    @Size(message = "高必须介于0~2147483647之间")
    @ApiModelProperty(value = "高 CM", required = true, dataType = "Double", position = 4, example = "10")
    private Double height;

    @NotBlank(message = "重量尺寸确认不能为空")
    @Length(max = 30, message = "重量尺寸确认不能超过30个字符")
    @ApiModelProperty(value = "重量尺寸确认，076001：仓库数据直接发货，076002：需要确认重量信息", required = true, dataType = "String", position = 5, example = "076001")
    private String packageConfirm;

    @Size(message = "重量误差范围必须介于0~2147483647之间")
    @ApiModelProperty(value = "重量误差范围 g", dataType = "Integer", position = 6, example = "50")
    private Integer packageWeightDeviation;

    @Length(max = 50, message = "增值税号不能超过50个字符")
    @ApiModelProperty(value = "增值税号", dataType = "String", position = 7, example = "F00X")
    private String ioss;

    @NotBlank(message = "物流服务不能为空")
    @Length(max = 50, message = "物流服务不能超过50个字符")
    @ApiModelProperty(value = "物流服务", dataType = "String", position = 8, example = "FX", extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name = "minlength", value = "0"),
                    @ExtensionProperty(name = "maxlength", value = "50")
            })
    })
    private String shipmentRule;

    @Length(max = 50, message = "参考号不能超过50个字符")
    @ApiModelProperty(value = "参考号", dataType = "String", position = 9, example = "")
    private String refNo;

    @Length(max = 500, message = "备注不能超过500个字符")
    @ApiModelProperty(value = "备注", dataType = "String", position = 10, example = "")
    private String remark;

    @Valid
    @ApiModelProperty(value = "地址信息", dataType = "DelOutboundAddressRequest", position = 11)
    private DelOutboundAddressRequest address;

    @Valid
    @ApiModelProperty(value = "明细信息", dataType = "DelOutboundDetailRequest", position = 12)
    private List<DelOutboundDetailRequest> details;
}
