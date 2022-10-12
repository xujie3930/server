package com.szmsd.finance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.bas.plugin.BasSubCommonPlugin;
import com.szmsd.bas.plugin.BasSubValueCommonParameter;
import com.szmsd.common.plugin.annotation.AutoFieldValue;
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
@TableName("fss_exchange_rate_log")
public class ExchangeRateLog extends FssBaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @AutoFieldValue(supports = BasSubCommonPlugin.SUPPORTS, nameField = "exchangeFrom", code = "008", cp = BasSubValueCommonParameter.class)
    @ApiModelProperty(value = "原币别id")
    private String exchangeFromCode;

    @AutoFieldValue(supports = BasSubCommonPlugin.SUPPORTS, nameField = "exchangeTo", code = "008", cp = BasSubValueCommonParameter.class)
    @ApiModelProperty(value = "现币别id")
    private String exchangeToCode;

    @ApiModelProperty(value = "原币别")
    private String exchangeFrom;

    @ApiModelProperty(value = "现币别")
    private String exchangeTo;

    @ApiModelProperty(value = "更新前汇率")
    private BigDecimal beforeRate;

    @ApiModelProperty(value = "更新后汇率")
    private BigDecimal afterRate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "失效时间")
    private Date expireTime;

    public ExchangeRateLog() {
    }

    public ExchangeRateLog(String exchangeFromCode, String exchangeToCode) {
        this.exchangeFromCode = exchangeFromCode;
        this.exchangeToCode = exchangeToCode;
    }

    @Override
    public boolean equals(Object obj) {
        ExchangeRateLog s=(ExchangeRateLog)obj;
        return exchangeFromCode.equals(s.exchangeFromCode)&&exchangeToCode.equals(s.exchangeToCode);
    }

    @Override
    public int hashCode() {
     String in=exchangeFromCode+exchangeToCode;
        return in.hashCode();
    }
}
