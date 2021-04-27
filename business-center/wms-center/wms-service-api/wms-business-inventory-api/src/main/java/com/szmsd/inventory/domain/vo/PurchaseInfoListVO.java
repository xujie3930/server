package com.szmsd.inventory.domain.vo;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;


/**
 * <p>
 * 采购单
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "Purchase对象")
public class PurchaseInfoListVO {

    @ApiModelProperty(value = "ID")
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "采购单号")
    @Excel(name = "采购单号")
    private String purchaseNo;

    @ApiModelProperty(value = "采购数量")
    @Excel(name = "采购数量")
    private Integer purchaseQuantity;

    @ApiModelProperty(value = "已创建入库单的数量")
    @Excel(name = "已创建入库单的数量")
    private Integer quantityInStorageCreated;
    @ApiModelProperty(value = "剩余的采购数量")
    @Excel(name = "剩余的采购数量")
    private Integer remainingPurchaseQuantity;
    @ApiModelProperty(value = "已到仓数量")
    @Excel(name = "已到仓数量")
    private Integer arrivalQuantity;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
