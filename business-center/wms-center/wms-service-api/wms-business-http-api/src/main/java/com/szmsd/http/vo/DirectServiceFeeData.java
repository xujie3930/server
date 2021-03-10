package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "DirectServiceFeeData")
public class DirectServiceFeeData {

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "产品代码")
    private String productCode;

    @ApiModelProperty(value = "发货渠道")
    private String shippingChannel;

    @ApiModelProperty(value = "时效最小值")
    private Integer timelinessMin;

    @ApiModelProperty(value = "时效最大值")
    private Integer timelinessMax;

    @ApiModelProperty(value = "金额记录")
    private ChargeModel chargeItems;

    @Data
    @Accessors(chain = true)
    public class ChargeModel {

        @ApiModelProperty
        private String totalAmount;

        @ApiModelProperty
        private List<PricingChargeItem> details;

        @Data
        @Accessors(chain = true)
        public class PricingChargeItem {

            @ApiModelProperty
            private String description;

            @ApiModelProperty
            private BigDecimal price;

            @ApiModelProperty
            private String currency;

        }
    }

    @ApiModelProperty
    private List<String> tags;

}
