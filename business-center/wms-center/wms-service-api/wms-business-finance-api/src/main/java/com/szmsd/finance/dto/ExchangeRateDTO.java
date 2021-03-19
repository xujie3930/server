package com.szmsd.finance.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author liulei
 */
@Data
@ApiModel(value = "FssExchangeRateDTO", description = "FssExchangeRateDTO对象")
public class ExchangeRateDTO implements Serializable {
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "原币别id")
    private Long exchangeFromId;

    @ApiModelProperty(value = "现币别id")
    private Long exchangeToId;

    @ApiModelProperty(value = "原币别")
    private String exchangeFrom;

    @ApiModelProperty(value = "现币别")
    private String exchangeTo;

    @ApiModelProperty(value = "比率")
    private BigDecimal rate;

    @ApiModelProperty(value = "备注")
    private String remark;
}
