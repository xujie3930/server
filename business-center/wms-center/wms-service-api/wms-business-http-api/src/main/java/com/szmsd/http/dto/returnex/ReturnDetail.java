package com.szmsd.http.dto.returnex;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Optional;

/**
 * @ClassName: ReturnDetail
 * @Description: 退货商品处理详情
 * @Author: 11
 * @Date: 2021/3/27 10:30
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "接收WMS传递的商品信息", description = "接收WMS传递的商品信息")
public class ReturnDetail implements Serializable {

    /**
     * 仓库登记SKU 1-50
     * /api/return/details G2
     */
    @ApiModelProperty(value = "仓库登记SKU")
    private String sku;

    @ApiModelProperty(value = "数量")
    private Integer qty;

    /**
     * SKU处理备注 0-500
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
