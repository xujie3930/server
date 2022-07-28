package com.szmsd.http.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 11:16
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "BulkOrderRequestDto", description = "BulkOrderRequestDto对象")
public class BulkOrderRequestDto implements Serializable {

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;


    @ApiModelProperty(value = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "发货规则（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "OMS出库单号")
    private String refOrderNo;

    @ApiModelProperty(value = "地址")
    private BulkOrderAddressRequestDto address;

    @ApiModelProperty(value = "箱号")
    private List<BulkOrderBoxRequestDto> boxList;


    @ApiModelProperty(value = "任务流程配置")
    private BulkOrderTaskConfigDto taskConfig;

}
