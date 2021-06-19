package com.szmsd.putinstorage.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "InboundReceiptQueryDTO", description = "入库查询入参")
public class InboundReceiptRecordQueryDTO {

    @ApiModelProperty(value = "入库单号")
    private String warehouseNo;

    @ApiModelProperty(value = "创建，提审，取消，审核，上架，完成")
    private String type;

}
