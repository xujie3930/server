package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "InventoryInboundDTO", description = "入库上架")
public class InventoryInboundDTO {

}
