package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "InventoryRecordQueryDTO", description = "InventoryRecordQueryDTO库存日志查询入参")
public class InventoryRecordQueryDTO {

    @ApiModelProperty(value = "单据号")
    private String receiptNo;

    @ApiModelProperty(value = "类型：1入库、2出库、3冻结、4盘点")
    private String type;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "创建时间（CR）")
    private TimeType timeType;

    @ApiModelProperty(value = "开始时间 - 由接口调用方定义")
    private String startTime;

    @ApiModelProperty(value = "结束时间 - 由接口调用方定义")
    private String endTime;

    @Getter
    @AllArgsConstructor
    public enum TimeType {
        /** 变动时间 **/
        OPERATE_ON("t.operate_on"),
        ;
        private String field;
    }

}
