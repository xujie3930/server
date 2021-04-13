package com.szmsd.delivery.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.delivery.dto.DelOutboundDetailDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "DelOutboundDetailListVO", description = "DelOutboundDetailListVO对象")
public class DelOutboundDetailListVO implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "出库单号")
    private String orderNo;

    @ApiModelProperty(value = "出库订单类型")
    private String orderType;

    @ApiModelProperty(value = "单据状态")
    private String state;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "发货规则（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

    @ApiModelProperty(value = "卖家代码")
    private String sellerCode;

    @ApiModelProperty(value = "挂号")
    private String trackingNo;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "明细信息")
    private List<DelOutboundDetailDto> details;

}
