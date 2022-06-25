package com.szmsd.chargerules.domain;

import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModelProperty;

public class ChaLevelMaintenanceDtoQuery extends QueryDto {
    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("等级名称")
    private String name;

    @ApiModelProperty("等级描述")
    private String description;
}
