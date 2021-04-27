package com.szmsd.inventory.domain.vo;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * 采购单日志
 * </p>
 *
 * @author 11
 * @since 2021-04-26
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "采购入库列表信息")
public class PurchaseStorageDetailsVO {

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Integer id;

    @ApiModelProperty(value = "关联的id")
    @Excel(name = "关联的id")
    private Integer associationId;

    @ApiModelProperty(value = "入库单号")
    @Excel(name = "入库单号")
    private String warehousingNo;

    @ApiModelProperty(value = "快递/揽收单号")
    @Excel(name = "快递/揽收单号")
    private String deliveryNo;

    @ApiModelProperty(value = "产品编号")
    @Excel(name = "产品编号")
    private String sku;

    @ApiModelProperty(value = "申报数量")
    @Excel(name = "申报数量")
    private Integer declareQty;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
