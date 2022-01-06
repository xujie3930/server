package com.szmsd.chargerules.domain;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;


/**
 * <p>
 * 费用规则明细表
 * </p>
 *
 * @author 11
 * @since 2021-11-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "费用规则明细表", description = "ChaOperationDetails对象")
public class ChaOperationDetails extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Long id;

    @ApiModelProperty(value = "规则id")
    @Excel(name = "规则id")
    private Long operationId;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "最小重量 - 开始 单位: g")
    @Excel(name = "最小重量 - 开始 单位: g")
    private BigDecimal minimumWeight;

    @ApiModelProperty(value = "最大重量 - 结束 单位: g")
    @Excel(name = "最大重量 - 结束 单位: g")
    private BigDecimal maximumWeight;

    @ApiModelProperty(value = "首件价格")
    @Excel(name = "首件价格")
    private BigDecimal firstPrice;

    @ApiModelProperty(value = "续件价格")
    @Excel(name = "续件价格")
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal nextPrice;

    @ApiModelProperty(value = "计费单位")
    @Excel(name = "计费单位")
    private String unit;

    @ApiModelProperty(value = "币种编码")
    @Excel(name = "币种编码")
    private String currencyCode;

    @ApiModelProperty(value = "币种名称")
    @Excel(name = "币种名称")
    private String currencyName;

    @ApiModelProperty(value = "折扣率")
    @Excel(name = "折扣率")
    private BigDecimal discountRate;


}
