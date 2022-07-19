package com.szmsd.delivery.dto;

import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.common.core.utils.FileStream;
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
 * @author zhouyonglai
 * @date 2022-07-19 18:20
 */
@Data
@ApiModel(value = "DelOutboundReceiveLabelDto", description = "DelOutboundReceiveLabelDto对象")
public class DelOutboundReceiveLabelDto implements Serializable {

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "refno")
    private String refNo;

    @ApiModelProperty(value = "原跟踪号")
    private String trackingNo;

    @ApiModelProperty(value = "接收跟踪号")
    private String traceId;


    @ApiModelProperty(value = "标签")
    @NotNull
    private FileStream fileStream;

    @ApiModelProperty(value = "备注")
    private String remark;




}
