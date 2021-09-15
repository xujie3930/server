package com.szmsd.doc.api.warehouse.req;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDetailDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;

@Validated
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CreateInboundReceiptDTO", description = "创建入库单")
public class CreateInboundReceiptReq extends InboundReceiptReq {
    @Valid
    @NotEmpty(message = "入库明细不能为空")
    @ApiModelProperty(value = "入库明细", required = true)
    private List<InboundReceiptDetailReq> inboundReceiptDetails;

    //    @ApiModelProperty(value = "要删除的入库明细id")
//    private List<String> receiptDetailIds;
    public void calculate() {
        if (CollectionUtils.isNotEmpty(inboundReceiptDetails)) {
            Integer integer = inboundReceiptDetails.stream().map(InboundReceiptDetailReq::getDeclareQty).filter(Objects::nonNull).reduce(Integer::sum).orElse(0);
            super.setTotalDeclareQty(integer);
        }
    }

    public CreateInboundReceiptReq checkOtherInfo() {
        // 不允许访问的对象
        if (!"Normal".equals(super.getOrderType())) {
            throw new CommonException("400", "订单类型异常!");
        }
        //055005 055006 055007
        String warehouseMethodCode = super.getWarehouseMethodCode();
        if ("055005".equals(warehouseMethodCode) || "055006".equals(warehouseMethodCode) || "055007".equals(warehouseMethodCode)) {
            throw new CommonException("400", "入库方式异常");
        }
        return this;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
