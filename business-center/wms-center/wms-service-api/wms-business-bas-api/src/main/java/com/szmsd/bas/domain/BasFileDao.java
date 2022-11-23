package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "BasFileDao", description = "BasFileDao对象")
public class BasFileDao extends QueryDto {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "0表示文件还未生成，1表示文件已经生成")
    private String state;
    @ApiModelProperty(value = "文件路径")
    private String fileRoute;

    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "模块区分(0:包裹查询)")
    private Integer modularType;

    @ApiModelProperty(value = "模块名中文")
    private String modularNameZh;

    @ApiModelProperty(value = "模块名英文")
    private String modularNameEn;

    @ApiModelProperty(value = "开始时间")
    @TableField(exist = false)
    private String startDate;

    @ApiModelProperty(value = "结束时间")
    @TableField(exist = false)
    private String EndDate;

    @ApiModelProperty(value = "模块名中文集合")
    @TableField(exist = false)
    private List<String> modularNameZhs;
}
