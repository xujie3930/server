package com.szmsd.doc.api.delivery.request;

import com.szmsd.doc.api.SwaggerDictionary;
import com.szmsd.doc.api.delivery.request.swagger.filter.OrderTypeDataFilter;
import com.szmsd.doc.validator.DictionaryPluginConstant;
import com.szmsd.doc.validator.annotation.Dictionary;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "DelOutboundListQueryRequest", description = "DelOutboundListQueryRequest对象")
public class DelOutboundListQueryRequest implements Serializable {

    @ApiModelProperty(value = "出库单号")
    private String orderNo;

    @ApiModelProperty(value = "挂号")
    private String trackingNo;

    @ApiModelProperty(value = "发货规则（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @Dictionary(message = "订单状态不存在", type = DictionaryPluginConstant.SUB_DICTIONARY_PLUGIN, param = "&&065")
    @SwaggerDictionary(dicCode = "065", dicKey = "subValue")
    @ApiModelProperty(value = "订单状态")
    private String state;

    @Dictionary(message = "订单状态不存在", type = DictionaryPluginConstant.SUB_DICTIONARY_PLUGIN, param = "&&063")
    @SwaggerDictionary(dicCode = "063", dicKey = "subValue", filter = OrderTypeDataFilter.class)
    @ApiModelProperty(value = "出库订单类型")
    private String orderType;

    @ApiModelProperty(value = "创建时间", example = "[\"2021-09-01\", \"2021-09-10\"]")
    private String[] createTimes;

    @ApiModelProperty("当前页，从1开始，默认为1")
    private int pageNum = 1;

    @ApiModelProperty("每页的数量，默认为10")
    private int pageSize = 10;
}
