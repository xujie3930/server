package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 10:54
 */
@Data
@ApiModel(value = "PackingRequirementInfoDto", description = "PackingRequirementInfoDto对象")
public class PackingRequirementInfoDto implements Serializable {

    @ApiModelProperty(value = "数量")
    private Long qty;

    @ApiModelProperty(value = "装箱要求")
    private List<ShipmentDetailInfoDto> details;
}
