package com.szmsd.returnex.domain;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


/**
* <p>
    * return_express - 退货单sku详情表
    * </p>
*
* @author 11
* @since 2021-03-29
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="return_express - 退货单sku详情表", description="ReturnExpressGood对象")
public class ReturnExpressGood extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private BigDecimal version;

    @ApiModelProperty(value = "关联退货单主键id")
    @Excel(name = "关联退货单主键id")
    private Integer associationId;

    @ApiModelProperty(value = "SKU")
    @Excel(name = "SKU")
    private String sku;

    @ApiModelProperty(value = "SKU到库数量")
    @Excel(name = "SKU到库数量")
    private Integer skuNumber;

    @ApiModelProperty(value = "仓库上架数量")
    @Excel(name = "仓库上架数量")
    private Integer warehouseQty;

    @ApiModelProperty(value = "上架数量")
    @Excel(name = "上架数量")
    private Integer putawayQty;

    @ApiModelProperty(value = "新上架编码")
    @Excel(name = "新上架编码")
    private String putawaySku;

    @ApiModelProperty(value = "SKU处理备注")
    @Excel(name = "SKU处理备注")
    private String processRemark;

    @ApiModelProperty(value = "逻辑删除")
    @Excel(name = "逻辑删除")
    private String delFlag;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
