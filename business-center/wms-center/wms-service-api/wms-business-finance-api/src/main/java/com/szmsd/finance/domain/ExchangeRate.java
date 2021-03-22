package com.szmsd.finance.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liulei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "汇率转换", description = "汇率转换表")
@TableName("fss_exchange_rate")
public class ExchangeRate extends FssBaseEntity {
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "原币别id")
    private String exchangeFromCode;

    @ApiModelProperty(value = "现币别id")
    private String exchangeToCode;

    @ApiModelProperty(value = "原币别")
    private String exchangeFrom;

    @ApiModelProperty(value = "现币别")
    private String exchangeTo;

    @ApiModelProperty(value = "比率")
    private BigDecimal rate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "失效时间")
    private Date expireTime;
}
