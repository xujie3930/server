package com.szmsd.inventory.domain.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @ClassName: TransportWarehousingAddDTO
 * @Description: zhuan
 * @Author: 11
 * @Date: 2021-04-27 18:01
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "转运入库新增对象")
public class TransportWarehousingAddDTO {

    @ApiModelProperty(value = "转运单id", required = true)
    private List<String> idList;

    @ApiModelProperty(value = "目标仓库code", required = true)
    @Excel(name = "目标仓库code")
    private String warehouseCode;

    @ApiModelProperty(value = "目标仓库", required = true)
    @Excel(name = "目标仓库")
    private String warehouseName;

    @ApiModelProperty(value = "出库方式编码", required = true)
    @Excel(name = "出库方式编码")
    private String orderType;

    @ApiModelProperty(value = "出库方式名", required = true)
    @Excel(name = "出库方式名")
    private String orderTypeName;

    @ApiModelProperty(value = "送货方式", required = true)
    @Excel(name = "送货方式")
    private String deliveryWayName;

    @ApiModelProperty(value = "送货方式编码", required = true)
    @Excel(name = "送货方式编码")
    private String deliveryWay;

    @ApiModelProperty(value = "快递/揽收单号")
    @Excel(name = "快递/揽收单号")
    private String deliveryNo;

    @ApiModelProperty(value = "VAT")
    @Excel(name = "VAT")
    private String vat;

    @ApiModelProperty(value = "类别")
    @Excel(name = "类别")
    private String warehouseCategoryName;

    @ApiModelProperty(value = "类别编码")
    @Excel(name = "类别编码")
    private String warehouseCategoryCode;

    @ApiModelProperty(value = "转运单列表", required = true)
    List<String> transferNoList;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

