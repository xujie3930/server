package com.szmsd.finance.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liulei
 */
@Data
@ApiModel(value = "FssExchangeRateDTO", description = "FssExchangeRateDTO对象")
public class ExchangeRateDTO implements Serializable {
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "原币别code")
    private String exchangeFromCode;

    @ApiModelProperty(value = "现币别code")
    private String exchangeToCode;

    @ApiModelProperty(value = "原币别")
    private String exchangeFrom;

    @ApiModelProperty(value = "现币别")
    private String exchangeTo;

    @ApiModelProperty(value = "比率")
    private BigDecimal rate;

    @ApiModelProperty(value = "备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "失效时间")
    private Date expireTime;
}
