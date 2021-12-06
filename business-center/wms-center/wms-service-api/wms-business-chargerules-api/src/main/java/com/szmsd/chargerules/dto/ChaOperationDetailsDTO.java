package com.szmsd.chargerules.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
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
@ApiModel(description = "ChaOperationDetails对象")
public class ChaOperationDetailsDTO implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @ExcelProperty(index = 0, value = "业务费用顺序")
    private Long rowId;

    @DecimalMin("0")
    @NotNull(message = "最小重量不能为空")
    @ExcelProperty(index = 1, value = "最小重量(g)")
    @ApiModelProperty(value = "最小重量 - 开始 单位: g", required = true)
    private BigDecimal minimumWeight;

    @DecimalMin("0")
    @NotNull
    @NotNull(message = "最大重量不能为空")
    @ExcelProperty(index = 2, value = "最大重量(g)")
    @ApiModelProperty(value = "最大重量 - 结束 单位: g", required = true)
    private BigDecimal maximumWeight;

    @NotNull(message = "计费单位不能为空")
    @ExcelProperty(index = 3, value = "计件单位")
    @ApiModelProperty(value = "计费单位", required = true)
    private String unit;

    @DecimalMin("0")
    @NotNull(message = "首件价格不能为空")
    @ExcelProperty(index = 4, value = "首件价格")
    @ApiModelProperty(value = "首件价格", required = true)
    private BigDecimal firstPrice;

    @DecimalMin("0")
    @ExcelProperty(index = 5, value = "续件价格")
    @ApiModelProperty(value = "续件价格")
    private BigDecimal nextPrice;

    @DecimalMin("0")
    @NotNull(message = "折扣率不能为空")
    @ExcelProperty(index = 6, value = "折扣率")
    @ApiModelProperty(value = "折扣率")
    private BigDecimal discountRate;


}
