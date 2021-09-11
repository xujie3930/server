package com.szmsd.doc.api.warehouse.req;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDetailDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Validated
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CreateInboundReceiptDTO", description = "创建入库单")
public class CreateInboundReceiptReq extends InboundReceiptReq {
    @Valid
    @ApiModelProperty(value = "入库明细")
    private List<InboundReceiptDetailDTO> inboundReceiptDetails;

//    @ApiModelProperty(value = "要删除的入库明细id")
//    private List<String> receiptDetailIds;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
