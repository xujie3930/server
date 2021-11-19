package com.szmsd.putinstorage.domain.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.utils.StringToolkit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Validated
@Data
@Accessors(chain = true)
@ApiModel(description = "更新运单号")
public class UpdateTrackingNoRequest {

    @NotBlank(message = "入库单号不能为空")
    @Size(max = 30, message = "入库单号仅支持0-30字符")
    @ApiModelProperty(value = "入库单号 (0-30]", required = true)
    private String warehouseNo;

    @NotBlank(message = "送货单号不能为空")
    @ApiModelProperty(value = "送货单号 可支持多个[,]拼接", required = true)
    private String deliveryNo;

    public UpdateTrackingNoRequest setDeliveryNo(String deliveryNo) {
        this.deliveryNo = deliveryNo;
        this.deliveryNoList = Optional.ofNullable(StringToolkit.getCodeByArray(deliveryNo)).orElse(new ArrayList<>()).stream().distinct().collect(Collectors.toList());
        return this;
    }

    @ApiModelProperty(value = "送货单号-多个", hidden = true)
    private List<String> deliveryNoList;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
