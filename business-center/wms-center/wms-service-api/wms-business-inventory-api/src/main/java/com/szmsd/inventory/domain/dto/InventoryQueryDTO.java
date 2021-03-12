package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "InventoryQueryDTO", description = "库存管理查询入参")
public class InventoryQueryDTO {

}
