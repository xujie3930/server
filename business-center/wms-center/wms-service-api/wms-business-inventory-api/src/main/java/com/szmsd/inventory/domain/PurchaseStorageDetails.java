package com.szmsd.inventory.domain;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;


/**
 * <p>
 * 采购单 入库详情
 * </p>
 *
 * @author 11
 * @since 2021-04-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "采购单 入库详情", description = "PurchaseStorageDetails对象")
public class PurchaseStorageDetails extends BaseEntity {

    private static final long serialVersionUID = 1L;

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

    @ApiModelProperty(value = "创建人编号")
    @Excel(name = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    @Excel(name = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private Integer version;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除")
    @Excel(name = "逻辑删除标识；2-已删除，0-未删除")
    private String delFlag;

    @ApiModelProperty(value = "快递/揽收单号")
    @Excel(name = "快递/揽收单号")
    private String deliveryNo;

    @ApiModelProperty(value = "产品编号")
    @Excel(name = "产品编号")
    private String sku;

    @ApiModelProperty(value = "申报数量")
    @Excel(name = "申报数量")
    private String declareQty;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
