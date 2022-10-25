package com.szmsd.delivery.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="查件服务", description="DelQueryService对象")
public class DelQueryServiceError {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单号/跟踪号")
    private String orderNoTraceid;

    @ApiModelProperty(value = "反馈类容")
    private String feedReason;

    @ApiModelProperty(value = "失败类容")
    private String errorMessage;

}