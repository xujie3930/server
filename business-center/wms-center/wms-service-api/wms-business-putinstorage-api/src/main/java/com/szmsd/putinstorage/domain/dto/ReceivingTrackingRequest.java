package com.szmsd.putinstorage.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value = "ReceivingTrackingRequest", description = "ReceivingTrackingRequest")
public class ReceivingTrackingRequest {

    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "操作时间")
    private Date operateOn;

    @NotBlank(message = "仓库代码不能为空")
    @Size(max = 50,message = "仓库代码只支持0-50字符")
    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @NotBlank(message = "单号不能为空")
    @Size(max = 50,message = "单号只支持0-50字符")
    @ApiModelProperty(value = "单号 - 入库单号")
    private String orderNo;

    @NotBlank(message = "挂号不能为空")
    @Size(max = 50,message = "挂号只支持0-50字符")
    @ApiModelProperty(value = "挂号")
    private String trackingNumber;

    @Size(max = 2000,message = "备注只支持0-200字符")
    @ApiModelProperty(value = "备注")
    private String remark;

}
