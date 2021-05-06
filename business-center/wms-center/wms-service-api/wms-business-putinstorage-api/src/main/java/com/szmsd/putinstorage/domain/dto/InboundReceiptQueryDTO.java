package com.szmsd.putinstorage.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "InboundReceiptQueryDTO", description = "入库查询入参")
public class InboundReceiptQueryDTO {

    @ApiModelProperty(value = "入库单号")
    private String warehouseNo;

    @ApiModelProperty(value = "入库单号")
    private List<String> warehouseNoList;

    @ApiModelProperty(value = "采购单")
    private String orderNo;

    @ApiModelProperty(value = "采购单")
    private List<String> orderNoList;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "普通入库（OMS用）：Normal" +
            "集运入库（OMS用）：Collection" +
            "包裹转运入库（OMS用）：PackageTransfer" +
            "新SKU入库（OMS用）：NewSku" +
            "上架入库（Yewu用）：Putaway" +
            "点数入库（Yewu用）：Counting")
    private String orderType;

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "状态0已取消，1初始，2已提审，3审核通过，-3审核失败，4处理中，5已完成")
    private String status;

    @ApiModelProperty(value = "状态集合")
    private List<String> statusList;

    @ApiModelProperty(value = "送货方式编码")
    private String deliveryWayCode;

    @ApiModelProperty(value = "创建时间（CR）")
    private TimeType timeType;

    @ApiModelProperty(value = "开始时间 - 由接口调用方定义")
    private String startTime;

    @ApiModelProperty(value = "结束时间 - 由接口调用方定义")
    private String endTime;

    @ApiModelProperty(value = "入库方式编码")
    private String warehouseMethodCode;

    @Getter
    @AllArgsConstructor
    public enum TimeType {
        /** 入库单创建时间 **/
        CR("t.create_time"),
        ;
        private String field;
    }

}
