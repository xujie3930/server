package com.szmsd.doc.api.region.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "", description = "地区对象")
public class RegionResp {

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "父节点id")
    private Integer pId;

    @ApiModelProperty(value = "地址类别:1国家 2省份 3市 4区 5街道")
    private Integer type;

    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "地址简码")
    private String addressCode;

    @ApiModelProperty(value = "英文名")
    private String enName;
}
