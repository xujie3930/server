package com.szmsd.delivery.dto;

import com.google.common.collect.Lists;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "DelOutboundListQueryDto", description = "DelOutboundListQueryDto对象")
public class DelOutboundListQueryDto extends QueryDto {

    @ApiModelProperty(value = "出库单号")
    private String orderNo;

    @ApiModelProperty(value = "采购单号")
    private String purchaseNo;

    @ApiModelProperty(value = "挂号")
    private String trackingNo;

    @ApiModelProperty(value = "发货规则（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "订单状态")
    private String state;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "出库订单类型")
    private String orderType;

    @ApiModelProperty(value = "创建时间")
    private String[] createTimes;

    @ApiModelProperty(value = "修改时间")
    private String updateTime;

    @ApiModelProperty(value = "选中的ID")
    private List<Long> selectIds;

    @ApiModelProperty(value = "重派")
    private String reassignType;

    @ApiModelProperty(value = "轨迹状态")
    private String trackingStatus;

    @ApiModelProperty(value = "当前语言")
    private String len;

    @ApiModelProperty(value = "refno")
    private String refNo;

    @ApiModelProperty(value = "查询所有订单类型（重派和非重派）")
    private Boolean queryAll;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除")
    private String delFlag;

    @ApiModelProperty(value = "国家代码")
    private String countryCode;


    @ApiModelProperty(value = "客户编码list")
    private List<String> customCodeList;

    public void setCustomCode(String customCode) {
        this.customCode = customCode;
        this.customCodeList = StringUtils.isNotEmpty(customCode) ? Arrays.asList(customCode.split(",")) : Lists.newArrayList();
    }

}
