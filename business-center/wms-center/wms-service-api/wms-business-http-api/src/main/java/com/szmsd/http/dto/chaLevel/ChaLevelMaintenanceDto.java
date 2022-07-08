package com.szmsd.http.dto.chaLevel;

import com.szmsd.http.vo.DateOperation;
import com.szmsd.http.vo.Operation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "ChaLevelMaintenanceReulst", description = "等级维护结果")
public class ChaLevelMaintenanceDto {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("等级名称")
    private String name;

    @ApiModelProperty("等级描述")
    private String description;

    @ApiModelProperty(value = "Creation")
    private DateOperation creation;
}
