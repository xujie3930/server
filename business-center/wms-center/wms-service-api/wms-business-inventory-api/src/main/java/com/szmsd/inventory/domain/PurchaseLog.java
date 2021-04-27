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
 * 采购单日志
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "采购单日志", description = "PurchaseLog对象")
public class PurchaseLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "关联的id集合")
    @Excel(name = "关联的id集合")
    private Integer associationId;

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

    @ApiModelProperty(value = "类型（0:创建采购单，1：创建入库单）")
    @Excel(name = "类型（0:创建采购单，1：创建入库单）")
    private Integer type;

    @ApiModelProperty(value = "出库单号")
    @Excel(name = "出库单号")
    private String orderNo;

    @ApiModelProperty(value = "采购单号")
    @Excel(name = "采购单号")
    private String purchaseNo;

    @ApiModelProperty(value = "入库单号")
    @Excel(name = "入库单号")
    private String warehouseNo;

    @ApiModelProperty(value = "日志内容")
    @Excel(name = "日志内容")
    private String logDetails;
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
