package com.szmsd.finance.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.finance.enums.CreditConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @ClassName: CreditInfoBO
 * @Description: 授信额度信息
 * @Author: 11
 * @Date: 2021-09-07 14:56
 */
@Setter
@Getter
@ApiModel(description = "授信额度信息")
public class CreditInfoBO {
    /**
     * 09-07 授信额度新增
     */
    @ApiModelProperty(value = "授信类型(0：额度，1：期限)")
    @Excel(name = "授信类型(0：额度，1：期限)")
    private Integer creditType;

    @ApiModelProperty(value = "授信状态（0：未启用，1：启用中，2：欠费停用，3：已禁用）")
    @Excel(name = "授信状态（0：未启用，1：启用中，2：欠费停用，3：已禁用）")
    private Integer creditStatus;

    @ApiModelProperty(value = "授信额度")
    @Excel(name = "授信额度")
    private BigDecimal creditLine;

    @ApiModelProperty(value = "使用额度金额")
    @Excel(name = "使用额度金额")
    private BigDecimal creditUseAmount;

    @ApiModelProperty(value = "授信开始时间")
    @Excel(name = "授信开始时间")
    private LocalDateTime creditBeginTime;

    @ApiModelProperty(value = "授信结束时间")
    @Excel(name = "授信结束时间")
    private LocalDateTime creditEndTime;

    @ApiModelProperty(value = "授信时间间隔")
    @Excel(name = "授信时间间隔")
    private Integer creditTimeInterval;

    @ApiModelProperty(value = "授信时间单位")
    @Excel(name = "授信时间单位")
    private String creditTimeUnit;

    @ApiModelProperty(value = "授信缓冲截止时间")
    @Excel(name = "授信缓冲截止时间")
    private LocalDateTime creditBufferTime;

    @ApiModelProperty(value = "授信缓冲时间间隔")
    @Excel(name = "授信缓冲时间间隔")
    private Integer creditBufferTimeInterval;

    @ApiModelProperty(value = "授信缓冲时间单位")
    @Excel(name = "授信缓冲时间单位")
    private String creditBufferTimeUnit;

    @ApiModelProperty(value = "缓冲时间使用额度")
    @Excel(name = "缓冲时间使用额度")
    private BigDecimal creditBufferUseAmount;

    /**
     * 使用信用额度扣费
     * 逾期/余额为0时 触发欠费 停用/禁用
     *
     * @param amount 扣完余额 要使用授信额度的金额
     * @return 是否可以扣费 且 修改授信额
     */
    protected boolean changeCreditAmount(BigDecimal amount,boolean updateCredit) {
        CreditConstant.CreditTypeEnum creditTypeEnum = CreditConstant.CreditTypeEnum.getThisByTypeCode(this.creditType);
        BigDecimal canUseAmount = this.creditLine.subtract(this.creditUseAmount);
        switch (creditTypeEnum) {
            case QUOTA:
                // 授信额度扣减 足额可扣减 反之不行
                if (amount.compareTo(canUseAmount) > 0) {
                    return false;
                } else {
                    // 刚好够扣则设置停用
                    if (amount.compareTo(canUseAmount) == 0) {
                        this.creditType = CreditConstant.CreditStatusEnum.ARREARAGE_DEACTIVATION.getValue();
                    }
                    if (updateCredit)
                    this.creditUseAmount = this.creditUseAmount.add(amount);
                    return true;
                }
            case TIME_LIMIT:
                // 余额 不足 但是在授信期间（7+3）则都可以支付
                LocalDateTime now = LocalDateTime.now();
                boolean after = now.isAfter(this.creditBeginTime);
                boolean before = now.isBefore(this.creditEndTime);
                if (after && before) {
                    if (updateCredit)
                    this.creditUseAmount = this.creditUseAmount.add(amount);
                    return true;
                } else {
                    //判断是否是 缓存期 缓冲期仍可下单 记录在缓冲期中作为下一期账单
                    boolean bufferBefore = now.isBefore(this.creditBufferTime);
                    if (after && bufferBefore) {
                        if (updateCredit)
                        this.creditBufferUseAmount = this.creditBufferUseAmount.add(amount);
                        return true;
                    } else {
                        // 逾期不还 禁用账号 充值所有金额才能继续使用
                        if (updateCredit)
                        this.creditStatus = CreditConstant.CreditStatusEnum.ARREARAGE_DEACTIVATION.getValue();
                        return false;
                    }
                }
            default:
                return false;
        }
    }

    /**
     * 充值（还款）授信额度
     *
     * @param amount
     * @return
     */
    public BigDecimal rechargeCreditAmount(BigDecimal amount) {
        LocalDateTime now = LocalDateTime.now();
        // 逾期禁用需要还清所有欠款，开启下次账期
        CreditConstant.CreditTypeEnum creditTypeEnum = CreditConstant.CreditTypeEnum.getThisByTypeCode(this.creditType);
        switch (creditTypeEnum) {
            case QUOTA:
                if (amount.compareTo(this.creditUseAmount) >= 0) {
                    amount = amount.subtract(this.creditUseAmount);
                    this.creditUseAmount = BigDecimal.ZERO;
                    this.creditStatus = CreditConstant.CreditStatusEnum.ACTIVE.getValue();
                    return amount;
                } else {
                    this.creditUseAmount = this.creditUseAmount.subtract(amount);
                    return BigDecimal.ZERO;
                }
            case TIME_LIMIT:
                // 提前还款
                if (now.isBefore(this.creditEndTime)) {
                    if (amount.compareTo(this.creditUseAmount) >= 0) {
                        //只还当前账期的 下一账期转出当前账期，开启下次账期
                        amount = amount.subtract(this.creditUseAmount);
                        this.creditUseAmount = BigDecimal.ZERO;
                        return amount;
                    } else {
                        this.creditUseAmount = this.creditUseAmount.subtract(amount);
                        return BigDecimal.ZERO;
                    }
                } else if (now.isBefore(this.creditBufferTime)) {
                    // 还款
                    if (amount.compareTo(this.creditUseAmount) >= 0) {
                        //只还当前账期的 下一账期转出当前账期，开启下次账期
                        amount = amount.subtract(this.creditUseAmount);
                        // 往后推 账期天数
                        ChronoUnit chronoUnit = null;
                        ChronoUnit bufferChronoUnit = null;
                        try {
                            chronoUnit = ChronoUnit.valueOf(this.creditTimeUnit);
                            bufferChronoUnit = ChronoUnit.valueOf(this.creditBufferTimeUnit);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                            chronoUnit = ChronoUnit.DAYS;
                            bufferChronoUnit = ChronoUnit.DAYS;
                        }

                        this.creditBeginTime = this.creditEndTime;
                        this.creditEndTime = this.creditEndTime.plus(this.creditTimeInterval, chronoUnit);
                        this.creditBufferTime = this.creditEndTime.plus(this.creditBufferTimeInterval, bufferChronoUnit);
                        this.creditUseAmount = this.creditBufferUseAmount;
                        this.creditStatus = CreditConstant.CreditStatusEnum.ACTIVE.getValue();
                        return amount;
                    } else {
                        this.creditUseAmount = this.creditUseAmount.subtract(amount);
                        return BigDecimal.ZERO;
                    }
                } else {
                    // 逾期还款
                    if (amount.compareTo(this.creditUseAmount) >= 0) {
                        // 充值金额优先还最先的欠款
                        amount = amount.subtract(this.creditUseAmount);
                        this.creditUseAmount = BigDecimal.ZERO;

                        // 如果是逾期不还则还需要把欠款全部补齐才能转到余额里面
                        if (amount.compareTo(this.creditBufferUseAmount) >= 0) {
                            // 还清所有的欠款  并 开启下次账期 激活账期
                            amount = amount.subtract(this.creditBufferUseAmount);

                            this.creditBufferUseAmount = BigDecimal.ZERO;
                            this.creditStatus = CreditConstant.CreditStatusEnum.ACTIVE.getValue();
                            // 往后推 账期天数
                            ChronoUnit chronoUnit = null;
                            ChronoUnit bufferChronoUnit = null;
                            try {
                                chronoUnit = ChronoUnit.valueOf(this.creditTimeUnit);
                                bufferChronoUnit = ChronoUnit.valueOf(this.creditBufferTimeUnit);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                                chronoUnit = ChronoUnit.DAYS;
                                bufferChronoUnit = ChronoUnit.DAYS;
                            }
                            this.creditBeginTime = now;
                            this.creditEndTime = now.plus(this.creditTimeInterval, chronoUnit);
                            this.creditBufferTime = creditEndTime.plus(this.creditBufferTimeInterval, bufferChronoUnit);
                            this.creditStatus = CreditConstant.CreditStatusEnum.ACTIVE.getValue();
                            return amount;
                        } else {
                            // 还完第一期 第二期不够还
                            this.creditBufferUseAmount = this.creditBufferUseAmount.subtract(amount);
                            return BigDecimal.ZERO;
                        }
                    } else {
                        // 充值不够还款 充值金额优先还最先的金额
                        this.creditUseAmount = this.creditUseAmount.subtract(amount);
                        return BigDecimal.ZERO;
                    }
                }
            default:
                return amount;
        }
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}