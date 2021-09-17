package com.szmsd.doc.api.warehouse.req;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.dto.WarehouseKvDTO;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.doc.api.AssertUtil400;
import com.szmsd.doc.component.IRemoterApi;
import com.szmsd.doc.component.RemoterApiImpl;
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
import java.util.ArrayList;
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
        List<String> warCodeList = new ArrayList<>();
        warCodeList.add("055001");
        warCodeList.add("055002");
        warCodeList.add("055003");
        warCodeList.add("055004");
        warCodeList.add("055008");
        boolean contains = warCodeList.contains(warehouseMethodCode);
        AssertUtil400.isTrue(contains, "入库方式异常");

        //类别
        String categoryCode = super.getWarehouseCategoryCode();
        List<String> categoryCodeList = new ArrayList<>();
        categoryCodeList.add("056001");
        categoryCodeList.add("056002");
        boolean containsCate = categoryCodeList.contains(categoryCode);
        AssertUtil400.isTrue(containsCate, "类别异常");

        // 送货方式
        String deliveryWayCode = super.getDeliveryWayCode();
        List<String> deliveryWayCodeList = new ArrayList<String>();
        deliveryWayCodeList.add("053001");
        deliveryWayCodeList.add("053002");
        deliveryWayCodeList.add("053003");
        boolean containsDeliveryWayCode = deliveryWayCodeList.contains(deliveryWayCode);
        AssertUtil400.isTrue(containsDeliveryWayCode, "送货方式异常");

        //产品货源地
        String goodsSourceCode = super.getGoodsSourceCode();
        AssertUtil400.isTrue("0".equals(goodsSourceCode) || "1".equals(goodsSourceCode), "产品货源地异常");
        // 裸货上架 过滤图片 TODO
        if ("055003".equals(super.getOrderType())){
            this.getInboundReceiptDetails().stream().map(InboundReceiptDetailReq::getEditionImage);
        }

        return this;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }


}
