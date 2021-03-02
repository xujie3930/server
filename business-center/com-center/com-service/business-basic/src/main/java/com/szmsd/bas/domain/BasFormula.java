package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * 公式表
 * </p>
 *
 * @author ziling
 * @since 2020-07-08
 */
@TableName("bas_formula")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasFormula对象", description = "公式表")
public class BasFormula  {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "重量段")
    @Excel(name = "重量段")
    private String weight;

    @ApiModelProperty(value = "报价id")
    @Excel(name = "报价id")
    private String cuspriceId;

    @ApiModelProperty(value = "公式")
    @Excel(name = "公式")
    private String calculateFormula;


}
