package com.szmsd.delivery.dto;

import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.common.core.validator.ValidationSaveGroup;
import com.szmsd.common.core.validator.ValidationUpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ApiModel(value = "UpdateWeightDelOutboundDto", description = "UpdateWeightDelOutboundDto对象")
public class UpdateWeightDelOutboundDto implements Serializable {

    @NotBlank(message = "orderNo不能为空")
    @ApiModelProperty(value = "orderNo")
    private String orderNo;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "包裹重量尺寸确认")
    private String packageConfirm;

    @ApiModelProperty(value = "包裹重量误差")
    @Min(value = 0, message = "包裹重量误差不能少于0")
    private Integer packageWeightDeviation;

    @ApiModelProperty(value = "长 CM")
    @DecimalMin(value = "0.01", message = "长 CM不能少于或者等于0")
    private Double length;

    @ApiModelProperty(value = "宽 CM")
    @DecimalMin(value = "0.01", message = "宽 CM不能少于或者等于0")
    private Double width;

    @ApiModelProperty(value = "高 CM")
    @DecimalMin(value = "0.01", message = "长高 CM不能少于或者等于0")
    private Double height;

    @ApiModelProperty(value = "重量 g")
    @DecimalMin(value = "0.01", message = "重量 g不能少于或者等于0")
    private Double weight;


}
