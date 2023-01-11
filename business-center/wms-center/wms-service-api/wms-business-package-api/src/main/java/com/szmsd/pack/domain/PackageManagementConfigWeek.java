package com.szmsd.pack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@ApiModel(value = "package - 交货管理 - 地址信息模板周数表", description = "PackageManagementConfigWeek对象")
public class PackageManagementConfigWeek {

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "模板id")
    private Integer packageManagementConfigId;

    @ApiModelProperty(value = "周")
    private String weekName;


}