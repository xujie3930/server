package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value="记泡规则从表", description="BasMeteringConfigData对象")
public class BasMeteringConfigData {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "计泡拦截主表id")
    private Integer meteringId;

    @ApiModelProperty(value = "重量类型一")
    private String weightTypeNameOne;

    @ApiModelProperty(value = "重量类型二")
    private String weightTypeNameTwo;

    @ApiModelProperty(value = "差异范围")
    private Integer differenceScope;

    @ApiModelProperty(value = "差异类型")
    @TableField(exist = false)
    private Integer differenceType;


}