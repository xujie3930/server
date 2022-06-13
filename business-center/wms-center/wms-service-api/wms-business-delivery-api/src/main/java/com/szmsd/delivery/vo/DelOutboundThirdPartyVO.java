package com.szmsd.delivery.vo;

import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.plugin.BasSubCommonPlugin;
import com.szmsd.bas.plugin.BasSubValueCommonParameter;
import com.szmsd.common.plugin.annotation.AutoFieldValue;
import com.szmsd.common.plugin.interfaces.DefaultCommonParameter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ApiModel(value = "DelOutboundVO", description = "DelOutboundVO对象")
public class DelOutboundThirdPartyVO implements Serializable {

    @AutoFieldValue(supports = BasSubCommonPlugin.SUPPORTS, code = "065", cp = BasSubValueCommonParameter.class)
    @ApiModelProperty(value = "单据状态")
    private String state;

    @ApiModelProperty(value = "单据状态名称")
    private String stateName;

    @ApiModelProperty(value = "长 CM")
    private Double length;

    @ApiModelProperty(value = "宽 CM")
    private Double width;

    @ApiModelProperty(value = "高 CM")
    private Double height;

    @ApiModelProperty(value = "重量 g")
    private Double weight;

    @ApiModelProperty(value = "规格")
    private String specifications;

    @ApiModelProperty(value = "异常描述")
    private String exceptionMessage;

}
