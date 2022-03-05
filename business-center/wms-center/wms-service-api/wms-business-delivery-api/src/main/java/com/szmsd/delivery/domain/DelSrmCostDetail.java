package com.szmsd.delivery.domain;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;


/**
* <p>
    * 出库单SRC成本明细
    * </p>
*
* @author Administrator
* @since 2022-03-04
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="出库单SRC成本明细", description="DelSrmCostDetail对象")
public class DelSrmCostDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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

    @ApiModelProperty(value = "跟踪ID")
    @Excel(name = "跟踪ID")
    private String traceId;

    @ApiModelProperty(value = "单号")
    @Excel(name = "单号")
    private String orderNo;

    @ApiModelProperty(value = "产品代码")
    @Excel(name = "产品代码")
    private String productCode;

    @ApiModelProperty(value = "PD号")
    @Excel(name = "PD号")
    private String pdCode;

    @ApiModelProperty(value = "报价编号")
    @Excel(name = "报价编号")
    private String cuspriceCode;

    @ApiModelProperty(value = "价格")
    @Excel(name = "价格")
    private BigDecimal amount;

    @ApiModelProperty(value = "货币编号")
    @Excel(name = "货币编号")
    private String currencyCode;

    @ApiModelProperty(value = "下单时间")
    @Excel(name = "下单时间")
    private Date orderTime;


    @TableField(exist = false)
    @ApiModelProperty(value = "创建时间")
    private String[] createTimes;

    @TableField(exist = false)
    @ApiModelProperty(value = "计价时间")
    private String[] orderTimes;

    @TableField(exist = false)
    @ApiModelProperty(value = "IDS")
    private Long[] ids;



}
