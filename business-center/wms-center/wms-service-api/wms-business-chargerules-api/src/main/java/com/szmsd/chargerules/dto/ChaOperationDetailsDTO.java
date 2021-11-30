package com.szmsd.chargerules.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


/**
 * <p>
 * 费用规则明细表
 * </p>
 *
 * @author 11
 * @since 2021-11-29
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "ChaOperationDetails对象")
public class ChaOperationDetailsDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @Excel(name = "id")
    private Long id;
    @DecimalMin("0")
    @NotNull
    @ApiModelProperty(value = "最小重量 - 开始 单位: g", required = true)
    @Excel(name = "最小重量 - 开始 单位: g")
    private BigDecimal minimumWeight;
    @DecimalMin("0")
    @NotNull
    @ApiModelProperty(value = "最大重量 - 结束 单位: g", required = true)
    @Excel(name = "最大重量 - 结束 单位: g")
    private BigDecimal maximumWeight;
    @DecimalMin("0")
    @NotNull
    @ApiModelProperty(value = "首件价格", required = true)
    @Excel(name = "首件价格")
    private BigDecimal firstPrice;
    @DecimalMin("0")
    @ApiModelProperty(value = "续件价格")
    @Excel(name = "续件价格")
    private BigDecimal nextPrice;
    @NotNull
    @ApiModelProperty(value = "计费单位", required = true)
    @Excel(name = "计费单位")
    private String unit;
    @DecimalMin("0")
    @ApiModelProperty(value = "折扣率")
    @Excel(name = "折扣率")
    private BigDecimal discountRate;


}
