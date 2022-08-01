package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-25 14:42
 */
@Data
@ApiModel(value = "DelOutboundBringVerifyNoDto", description = "DelOutboundBringVerifyNoDto对象")
public class DelOutboundBringVerifyNoDto implements Serializable {

    @NotNull(message = "orderNos不能为空")
    @ApiModelProperty(value = "orderNos")
    private List<String> orderNos;
}
