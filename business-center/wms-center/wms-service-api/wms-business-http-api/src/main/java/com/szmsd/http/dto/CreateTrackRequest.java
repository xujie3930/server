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
 * @Description: 创建入库单物流信息列表
 * @Author: 11
 * @Date: 2021-04-27 19:25
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "创建入库单物流信息列表")
public class CreateTrackRequest {
    /**
     * 仓库代码
     **/
    @ApiModelProperty("仓库代码")
    private String warehouseCode;

    @ApiModelProperty("挂号列表(每次创建/修改都传完整的挂号列表)")
    private List<String> trackingNumberList ;

    @ApiModelProperty("参照单号（传OMS单号）")
    private String refOrderNo;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
