package com.szmsd.doc.api.warehouse.req;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDetailDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Validated
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(description = "入库单对象")
public class BatchInboundReceiptReq {

    @Valid
    @NotEmpty(message = "入库单不能为空")
    @ApiModelProperty(value = "入库单", required = true)
    private List<CreateInboundReceiptReq> batchInboundReceiptList;


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
