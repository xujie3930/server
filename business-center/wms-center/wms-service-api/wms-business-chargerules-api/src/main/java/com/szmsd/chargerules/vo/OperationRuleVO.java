package com.szmsd.chargerules.vo;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import com.szmsd.chargerules.enums.OrderTypeEnum;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "业务操作", description = "业务操作表")
public class OperationRuleVO {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "操作类型")
    private String operationType;

    @ApiModelProperty(value = "操作类型名称")
    private String operationTypeName;

    @ApiModelProperty(value = "最小重量 - 开始 单位: g 大于")
    private Double minimumWeight;

    @ApiModelProperty(value = "最大重量 - 结束 单位: g 小于等于")
    private Double maximumWeight;

    @ApiModelProperty(value = "仓库")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "订单类型")
    private String orderType;

    @ApiModelProperty(value = "订单类型名称")
    private String orderTypeName;

    @ApiModelProperty(value = "首件价格")
    private BigDecimal firstPrice;

    @ApiModelProperty(value = "续件价格")
    private BigDecimal nextPrice;

    @ApiModelProperty(value = "币种编码")
    private String currencyCode;

    @ApiModelProperty(value = "币种名称")
    private String currencyName;

    @ApiModelProperty(value = "计费单位")
    private String unit;

    @ApiModelProperty(value = "折扣率")
    private BigDecimal discountRate;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
