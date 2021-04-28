package com.szmsd.http.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @ClassName: CreatePackageReceiptRequest
 * @Description: 创建转运入库单
 * @Author: 11
 * @Date: 2021-04-27 19:25
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "创建转运入库单")
public class CreatePackageReceiptRequest {
    /**
     * 仓库代码
     **/
    @ApiModelProperty("仓库代码")
    private String warehouseCode;

    @ApiModelProperty("入库单类型 包裹转运入库（OMS用）：PackageTransfer")
    private String orderType = "PackageTransfer";

    @ApiModelProperty("卖家代码")
    private String sellerCode;

    @ApiModelProperty("挂号")
    private String trackingNumber;

    @ApiModelProperty("下单备注")
    private String remark;

    @ApiModelProperty("参照单号（传OMS单号）")
    private String refOrderNo;

    @ApiModelProperty("收货单包裹明细")
    private List<ReceiptDetailPackageInfo> detailPackages;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
