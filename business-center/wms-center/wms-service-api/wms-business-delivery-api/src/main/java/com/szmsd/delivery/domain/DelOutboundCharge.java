package com.szmsd.delivery.domain;

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
 * 出库单费用明细
 * </p>
 *
 * @author asd
 * @since 2021-04-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "出库单费用明细", description = "DelOutboundCharge对象")
public class DelOutboundCharge extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Long id;

    @ApiModelProperty(value = "创建人编号")
    @Excel(name = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    @Excel(name = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private Long version;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除")
    @Excel(name = "逻辑删除标识；2-已删除，0-未删除")
    private String delFlag;

    @ApiModelProperty(value = "跟踪ID")
    @Excel(name = "跟踪ID")
    private String traceId;

    @ApiModelProperty(value = "单号")
    @Excel(name = "单号")
    private String orderNo;

    @ApiModelProperty(value = "费用编号")
    @Excel(name = "费用编号")
    private String billingNo;

    @ApiModelProperty(value = "费用名称（中文）")
    @Excel(name = "费用名称（中文）")
    private String chargeNameCn;

    @ApiModelProperty(value = "费用名称（英文）")
    @Excel(name = "费用名称（英文）")
    private String chargeNameEn;

    @ApiModelProperty(value = "费用编号")
    @Excel(name = "费用编号")
    private String parentBillingNo;

    @ApiModelProperty(value = "费用")
    @Excel(name = "费用")
    private BigDecimal amount;

    @ApiModelProperty(value = "币种")
    @Excel(name = "币种")
    private String currencyCode;

}
