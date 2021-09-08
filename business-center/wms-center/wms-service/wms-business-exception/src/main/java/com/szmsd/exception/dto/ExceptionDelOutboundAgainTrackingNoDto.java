package com.szmsd.exception.dto;

import com.szmsd.delivery.dto.DelOutboundAgainTrackingNoDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "ExceptionDelOutboundAgainTrackingNoDto", description = "ExceptionDelOutboundAgainTrackingNoDto对象")
public class ExceptionDelOutboundAgainTrackingNoDto extends DelOutboundAgainTrackingNoDto {

    @NotNull(message = "异常ID不能为空")
    @ApiModelProperty(value = "异常ID")
    private Integer exceptionId;
}
