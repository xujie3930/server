package com.szmsd.http.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @ClassName: ReceiptDetailPackageInfo
 * @Description: 收货单包裹明细
 * @Author: 11
 * @Date: 2021-04-27 19:28
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "收货单包裹明细")
public class ReceiptDetailPackageInfo {
    @ApiModelProperty("出库包裹单号")
    private String packageOrderNo;
    @ApiModelProperty("出库包裹扫描编码（实物上的课扫描编码，一般填写出库单号）")
    private String scanCode;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
