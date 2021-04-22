package com.szmsd.http.dto.returnex;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName: ReturnDetail
 * @Description: 退货商品处理详情
 * @Author: 11
 * @Date: 2021/3/27 10:30
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "WMS接收商品处理详情", description = "WMS接收商品处理详情")
public class ReturnDetailWMS implements Serializable {

    /**
     * 仓库登记SKU 1-50
     * /api/return/details G2
     */
    @ApiModelProperty(value = "仓库登记SKU")
    private String sku;


    /*通知WMS参数*/
    /**
     * 新上架编码 0-50
     */
    @ApiModelProperty(value = "新上架编码")
    private String putawaySku;

    /**
     * 新上架编码 1-99999
     */
    @ApiModelProperty(value = "上架数量")
    private Integer putawayQty;

    /**
     * SKU处理备注 0-500
     */
    @ApiModelProperty(value = "SKU处理备注")
    private String processRemark;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
