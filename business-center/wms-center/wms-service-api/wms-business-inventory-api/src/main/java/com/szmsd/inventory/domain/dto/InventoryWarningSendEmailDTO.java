package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "InventoryWarningQueryDTO", description = "库存对账 - 查询入参")
public class InventoryWarningSendEmailDTO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "批次号")
    private String batchNo;

    @ApiModelProperty(value = "收件邮箱：不填发送系统指定邮箱")
    private String toEmail;

}
