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


/**
* <p>
    * 
    * </p>
*
* @author l
* @since 2021-03-06
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="", description="BasPacking对象")
public class BasePacking extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Long id;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "删除标识：0未删除 1已删除")
    private String delFlag;

    @ApiModelProperty(value = "物料名称")
    @Excel(name = "物料名称")
    @TableField("`name`")
    private String name;

    @ApiModelProperty(value = "物料编码")
    @Excel(name = "物料编码")
    @TableField("`code`")
    private String code;

    @ApiModelProperty(value = "客户（卖家）编码")
    @Excel(name = "客户（卖家）编码")
    private String sellerCode;

    @ApiModelProperty(value = "类型")
    @Excel(name = "类型")
    private String category;

    @ApiModelProperty(value = "价格")
    @Excel(name = "价格")
    private Double price;

    @ApiModelProperty(value = "优先值")
    @Excel(name = "优先值")
    private Integer priorityLevel;

    @ApiModelProperty(value = "类型名")
    @Excel(name = "类型名")
    private String categoryName;

    @ApiModelProperty(value = "描述")
    @Excel(name = "描述")
    @TableField("`describe`")
    private String describe;

    @ApiModelProperty(value = "属性1")
    @Excel(name = "属性1")
    private String attribute1;

    @ApiModelProperty(value = "属性2")
    @Excel(name = "属性2")
    private String attribute2;

    @ApiModelProperty(value = "属性3")
    @Excel(name = "属性3")
    private String attribute3;

    @ApiModelProperty(value = "属性4")
    @Excel(name = "属性4")
    private String attribute4;

    @ApiModelProperty(value = "属性5")
    @Excel(name = "属性5")
    private String attribute5;

    @ApiModelProperty(value = "父id")
    @Excel(name = "父id")
    private Long pId;

    @ApiModelProperty(value = "是否激活")
    @Excel(name = "是否激活")
    private Boolean isActive;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名")
    private String warehouseName;

}
