package com.szmsd.putinstorage.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "CreateInboundReceiptDTO", description = "创建入库单")
public class CreateInboundReceiptDTO extends InboundReceiptDTO {

    @ApiModelProperty(value = "入库明细")
    private List<InboundReceiptDetailDTO> inboundReceiptDetailDTOS;

}
