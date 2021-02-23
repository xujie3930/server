package com.szmsd.websocket.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.annotation.Excel.ColumnType;
import com.szmsd.common.core.annotation.Excel.Type;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Date;

@TableName("test")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "test对象", description = "test")
public class Test extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键id", cellType = ColumnType.NUMERIC)
    @ApiModelProperty(value = "主键id")
    private Long id;

    @Excel(name = "名称")
    @ApiModelProperty(value = "名称")
    private String name;

    @Excel(name = "性别：0=男，1=女")
    @ApiModelProperty(value = "性别：0=男，1=女")
    private Long sex;

    @Excel(name = "笔记")
    @ApiModelProperty(value = "笔记")
    private String note;

    @Excel(name = "年龄")
    @ApiModelProperty(value = "年龄")
    @NotNull
    private Long age;



    @Excel(name = "状态（0正常 1停用）")
    @ApiModelProperty(value = "状态（0正常 1停用）")
    private String status;
    @Excel(name = "版本号")
    @ApiModelProperty(value = "版本号")
    private String version;

    @Excel(name = "删除标志（0代表存在 2代表删除）")
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;
    @ApiModelProperty(value = "创建者ID")
    private String createId;
    @Excel(name = "删除标志（0代表存在 2代表删除）", type = Type.EXPORT)
    @ApiModelProperty(value = "创建者")
    private String createBy;


    @ApiModelProperty(value = "更新者ID")
    private String updateBy;


}
