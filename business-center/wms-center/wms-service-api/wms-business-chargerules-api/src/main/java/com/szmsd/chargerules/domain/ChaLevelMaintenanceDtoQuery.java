package com.szmsd.chargerules.domain;

import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "等级表", description = "ChaLevelMaintenanceDtoQuery")
public class ChaLevelMaintenanceDtoQuery extends QueryDto {
    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("等级名称")
    private String name;

    @ApiModelProperty("等级描述")
    private String description;
}
