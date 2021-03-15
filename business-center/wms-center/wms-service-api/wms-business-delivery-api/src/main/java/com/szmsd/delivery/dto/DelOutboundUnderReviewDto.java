package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ApiModel(value = "DelOutboundUnderReviewDto", description = "DelOutboundUnderReviewDto对象")
public class DelOutboundUnderReviewDto implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

}
