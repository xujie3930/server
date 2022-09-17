package com.szmsd.delivery.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:23
 */
@Data
@ApiModel(value = "DelOutboundCollectionDetailImportDto2", description = "DelOutboundCollectionDetailImportDto2对象")
public class DelOutboundCollectionDetailImportDto2 implements Serializable {


    @ApiModelProperty(value = "订单序号\n" +
            "（必填）")
    private Integer sort;

    @ApiModelProperty(value = "产品编码\n" +
            "（必填）")
    private String code;

    @ApiModelProperty(value = "数量\n" +
            "（必填）")
    private Integer qty;

}
