package com.szmsd.finance.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.finance.enums.CreditConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @ClassName: CreditInfoBO
 * @Description: 授信额度信息 期限不需要关联币别 额度需要绑定币别
 * 为了好扩展 期限也按照授信额度一样的逻辑，多冗余别的币别一套数据
 * @Author: 11
 * @Date: 2021-09-07 14:56
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "用户授信额度详情信息")
public class UserCreditDetailDTO {

    @NotNull(message = "授信类型不能为空")
    @ApiModelProperty(value = "授信类型(QUOTA：额度，TIME_LIMIT：期限)")
    @Excel(name = "授信类型(QUOTA：额度，TIME_LIMIT：期限)")
    private CreditConstant.CreditTypeEnum creditType;

    @NotNull(groups = Quota.class, message = "授信额度不能为空")
    @DecimalMin(value = "0", message = "授信额度不能小于0")
    @ApiModelProperty(value = "授信额度")
    @Excel(name = "授信额度")
    private BigDecimal creditLine;

    @Min(value = 3, groups = Interval.class, message = "授信时间最小为3天")
    @NotNull(groups = Interval.class, message = "授信时间不能为空")
    @ApiModelProperty(value = "授信时间间隔")
    @Excel(name = "授信时间间隔")
    private Integer creditTimeInterval;

    @NotBlank(groups = Quota.class, message = "币种不能为空")
    @ApiModelProperty(value = "币种编码")
    private String currencyCode;

//    @NotBlank(groups = Quota.class, message = "币种名不能为空")
    @ApiModelProperty(value = "币种名")
    private String currencyName;

    @ApiModelProperty(value = "授信开始时间", hidden = true)
    @Excel(name = "授信开始时间")
    private LocalDateTime creditBeginTime;

    @ApiModelProperty(value = "授信结束时间", hidden = true)
    @Excel(name = "授信结束时间")
    private LocalDateTime creditEndTime;

    @ApiModelProperty(value = "授信时间单位", hidden = true)
    @Excel(name = "授信时间单位")
    private String creditTimeUnit;

    @ApiModelProperty(value = "授信缓冲截止时间", hidden = true)
    @Excel(name = "授信缓冲截止时间")
    private LocalDateTime creditBufferTime;

    @ApiModelProperty(value = "授信缓冲时间间隔", hidden = true)
    @Excel(name = "授信缓冲时间间隔")
    private Integer creditBufferTimeInterval;

    @ApiModelProperty(value = "授信缓冲时间单位", hidden = true)
    @Excel(name = "授信缓冲时间单位")
    private String creditBufferTimeUnit;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

interface Interval {

}

interface Quota {

}
