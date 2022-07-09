package com.szmsd.delivery.dto;

import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.common.core.validator.ValidationSaveGroup;
import com.szmsd.common.core.validator.ValidationUpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @NotNull(message = "orderNo不能为空", groups = ValidationUpdateGroup.class)
    @ApiModelProperty(value = "orderNo")
    private String orderNo;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "包裹重量尺寸确认")
    private String packageConfirm;

    @ApiModelProperty(value = "包裹重量误差")
    private Integer packageWeightDeviation;

    @ApiModelProperty(value = "长 CM")
    private Double length;

    @ApiModelProperty(value = "宽 CM")
    private Double width;

    @ApiModelProperty(value = "高 CM")
    private Double height;

    @ApiModelProperty(value = "重量 g")
    private Double weight;


}
