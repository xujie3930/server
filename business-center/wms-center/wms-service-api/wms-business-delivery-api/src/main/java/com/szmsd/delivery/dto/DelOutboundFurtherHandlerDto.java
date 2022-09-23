package com.szmsd.delivery.dto;

import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.szmsd.delivery.domain.DelOutboundAddress;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-25 14:42
 */
@Data
@ApiModel(value = "DelOutboundFurtherHandlerDto", description = "DelOutboundFurtherHandlerDto对象")
public class DelOutboundFurtherHandlerDto implements Serializable {

    @NotEmpty(message = "订单号不能为空")
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "发货规则（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

//    @ApiModelProperty(value = "发货服务名称")
    private String shipmentService;

//    @ApiModelProperty(value = "地址")
    private DelOutboundAddress delOutboundAddress;

    @ApiModelProperty(value = "失败了是否推送发货指令，默认false不推送")
    private boolean shipmentShipping;

    //是否执行更新发货指令接口调用,供应商等
    private boolean execShipmentShipping;


    /**
     集合
     **/
    @ExcelCollection(name = "出库单sku明细")
    private List<DelOutboundAddress> detailList;
}
