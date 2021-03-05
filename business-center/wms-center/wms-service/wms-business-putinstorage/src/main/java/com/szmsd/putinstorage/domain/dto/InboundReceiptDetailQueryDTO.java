package com.szmsd.putinstorage.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "InboundReceiptDetailQueryDTO", description = "入库明细入参")
public class InboundReceiptDetailQueryDTO {
}
