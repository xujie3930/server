package com.szmsd.putinstorage.domain;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.szmsd.common.core.web.domain.BaseEntity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;


/**
 * <p>
 * 入库物流到货记录
 * </p>
 *
 * @author 11
 * @since 2021-09-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "入库物流到货记录", description = "InboundTracking对象")
public class InboundTracking extends BaseEntity {

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

    @ApiModelProperty(value = "入库单号")
    @Excel(name = "入库单号")
    private String orderNo;

    @ApiModelProperty(value = "客户编码")
    @Excel(name = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "目的仓库编码")
    @Excel(name = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "挂号")
    @Excel(name = "挂号")
    private String trackingNumber;

    @ApiModelProperty(value = "操作人")
    @Excel(name = "操作人")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    @Excel(name = "操作时间")
    private Date operateOn;


}
