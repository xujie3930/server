package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="渠道节点维护主表", description="BasChannel对象")
public class BasChannels  extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "国家名称")
    @Excel(name = "国家名称")
    private String countryName;

    @ApiModelProperty(value = "国家简码")
    @Excel(name = "国家简码")
    private String countryCode;

    @ApiModelProperty(value = "物流服务code")
    @Excel(name = "物流服务code")
    private String logisticsErvicesCode;

    @ApiModelProperty(value = "物流服务名")
    @Excel(name = "物流服务名")
    private String logisticsErvicesName;

//    @ApiModelProperty(value = "仓库code")
//    @Excel(name = "仓库code")
//    @TableField(exist = false)
//    private String warehouseCode;

    @ApiModelProperty(value = "仓库名(主界面显示用逗号拼接的)")
    @Excel(name = "仓库名")
    @TableField(exist = false)
    private String warehouseName;

    @ApiModelProperty(value = "预计整体时效")
    @Excel(name = "预计整体时效")
    private String estimateWholeTime;

    @ApiModelProperty(value = "创建人编号")
    @Excel(name = "创建人编号")
    private String createBy;


    @ApiModelProperty(value = "修改人编号")
    @Excel(name = "修改人编号")
    private String updateBy;


    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "删除标识：0未删除 2已删除")
    private String delFlag;

    @ApiModelProperty(value = "从表数据")
    @TableField(exist = false)
    private List<BasChannelDate> basChannelDateList;

//    @ApiModelProperty(value = "下拉框选的仓库code集合")
//    @TableField(exist = false)
//    private List<String> warehouseCodeList;



    @ApiModelProperty(value = "下拉框选的仓库集合")
    @TableField(exist = false)
    private List<BasChannelWarehouse> warehouseList;


}