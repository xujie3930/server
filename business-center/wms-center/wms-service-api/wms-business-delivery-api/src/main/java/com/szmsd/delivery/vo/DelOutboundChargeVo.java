package com.szmsd.delivery.vo;

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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 出库单费用明细
 * </p>
 *
 * @author asd
 * @since 2021-04-01
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "出库单费用明细导出", description = "DelOutboundChargeVo对象")
public class DelOutboundChargeVo  {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "单号")
    @Excel(name = "订单号")
    private String orderNo;



    @ApiModelProperty(value = "跟踪号")
    @Excel(name = "跟踪号")
    @TableField(exist = false)
    private String trackingNo;


    @ApiModelProperty(value = "refNo")
    @Excel(name = "refNo")
    @TableField(exist = false)
    private String refNo;


    @ApiModelProperty("客户代码")
    @Excel(name = "客户代码")
    @TableField(exist = false)
    private String sellerCode;

    @ApiModelProperty(value = "币种")
    @Excel(name = "币种")
    private String currencyCode;



    @ApiModelProperty(value = "总金额")

    @Excel(name = "总金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "扣费明细")
    @Excel(name = "扣费明细")
    private String remark;

    @Excel(name = "结算时间")
    @ApiModelProperty(value = "结算时间")
    private String createTime;


}
