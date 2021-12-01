package com.szmsd.chargerules.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.chargerules.enums.DelOutboundOrderEnum;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.exception.com.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "OperationQueryDTO")
public class OperationQueryDTO implements Serializable {

    @ApiModelProperty(value = "仓库")
    private String warehouseCode;

    @ApiModelProperty(value = "操作类型")
    private String operationType;

    @ApiModelProperty(value = "订单类型")
    private String orderType;

    @ApiModelProperty(value = "客户类型编码")
    private String cusTypeCode;

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "生效时间")
    private LocalDateTime effectiveTime;

    @ApiModelProperty(value = "失效时间")
    private LocalDateTime expirationTime;

    @ApiModelProperty(value = "币别编码")
    private String currencyCode;

}
