package com.szmsd.chargerules.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.chargerules.enums.DelOutboundOrderEnum;
import com.szmsd.chargerules.enums.OrderTypeEnum;
import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "OperationQueryDTO")
public class OperationQueryDTO extends QueryDto implements Serializable {

    @ApiModelProperty(value = "仓库")
    private String warehouseCode;
    /**
     * {@link OrderTypeEnum}
     */
    @ApiModelProperty(value = "操作类型")
    private String operationType;

    /**
     * {@link DelOutboundOrderEnum}
     */
    @ApiModelProperty(value = "订单类型")
    private String orderType;

    @ApiModelProperty(value = "客户类型编码")
    private String cusTypeCode;

    @ApiModelProperty(value = "客户编码(,拼接)")
    private String cusCodeList;

    @ApiModelProperty(value = "生效时间")
    private LocalDateTime effectiveTime;

    @ApiModelProperty(value = "失效时间")
    private LocalDateTime expirationTime;

    @ApiModelProperty(value = "币别编码")
    private String currencyCode;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
