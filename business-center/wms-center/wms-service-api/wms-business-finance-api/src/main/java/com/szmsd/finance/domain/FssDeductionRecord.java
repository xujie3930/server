package com.szmsd.finance.domain;

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
    * 扣费信息记录表
    * </p>
*
* @author 11
* @since 2021-10-14
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="扣费信息记录表", description="FssDeductionRecord对象")
public class FssDeductionRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
            @TableId(value = "id", type = IdType.UUID)
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

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String cusCode;

    @ApiModelProperty(value = "订单号")
    @Excel(name = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "交易类型")
    @Excel(name = "交易类型")
    private String payMethod;

    @ApiModelProperty(value = "产品类别")
    @Excel(name = "产品类别")
    private String productCategory;

    @ApiModelProperty(value = "费用类别")
    @Excel(name = "费用类别")
    private String chargeCategory;

    @ApiModelProperty(value = "费用类型")
    @Excel(name = "费用类型")
    private String chargeType;

    @ApiModelProperty(value = "订单金额")
    @Excel(name = "订单金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "订单币别")
    @Excel(name = "订单币别")
    private String currencyCode;

    @ApiModelProperty(value = "实际扣费金额(余额扣费)")
    @Excel(name = "实际扣费金额(余额扣费)")
    private BigDecimal actualDeduction;

    @ApiModelProperty(value = "使用授信金额")
    @Excel(name = "使用授信金额")
    private BigDecimal creditUseAmount;

    @ApiModelProperty(value = "使用授信类型")
    @Excel(name = "使用授信类型")
    private Integer creditType;

    @ApiModelProperty(value = "授信开始时间")
    @Excel(name = "授信开始时间")
    private Date creditBeginTime;

    @ApiModelProperty(value = "授信结束时间")
    @Excel(name = "授信结束时间")
    private Date creditEndTime;

    @ApiModelProperty(value = "还款金额")
    @Excel(name = "还款金额")
    private BigDecimal repaymentAmount;

    @ApiModelProperty(value = "剩余还款金额")
    @Excel(name = "剩余还款金额")
    private BigDecimal remainingRepaymentAmount;

    @ApiModelProperty(value = "扣费类型（0=扣款,1=还款）")
    @Excel(name = "扣费类型（0=扣款,1=还款）")
    private Integer type;


}
