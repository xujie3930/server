package com.szmsd.putinstorage.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel(value = "InboundCountVO", description = "入库单统计")
public class InboundCountVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户代号")
    private String cusCode;

    @ApiModelProperty(value = "分组")
    private String groupBy;

    @ApiModelProperty(value = "数量")
    private Integer count;

    @ApiModelProperty(value = "数量2")
    private Integer count2;

}
