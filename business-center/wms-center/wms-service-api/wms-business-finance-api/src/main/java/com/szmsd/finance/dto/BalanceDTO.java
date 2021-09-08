package com.szmsd.finance.dto;

import com.szmsd.finance.enums.CreditConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author liulei
 */
@Data
public class BalanceDTO {
    @ApiModelProperty(value = "可用余额")
    private BigDecimal currentBalance;

    @ApiModelProperty(value = "冻结余额")
    private BigDecimal freezeBalance;

    @ApiModelProperty(value = "总余额")
    private BigDecimal totalBalance;

    @ApiModelProperty(value = "授信信息")
    private CreditInfoBO creditInfoBO;

    private BalanceDTO() {
    }

    public BalanceDTO(BigDecimal currentBalance, BigDecimal freezeBalance, BigDecimal totalBalance) {
        this.currentBalance = currentBalance;
        this.freezeBalance = freezeBalance;
        this.totalBalance = totalBalance;
        this.creditInfoBO = new CreditInfoBO();
    }

    /**
     * 校验并设置余额
     * 有授信额度 则先扣余额 然后扣除授信额度 / 授信账期
     *
     * @param amount 余额
     * @return 处理结果
     */
    public Boolean checkAmountAndCreditAndSet(BigDecimal amount, boolean updateCredit, BiFunction<BalanceDTO, BigDecimal, Boolean> function) {
        CreditInfoBO creditInfoBO = this.creditInfoBO;
        Integer creditStatus = creditInfoBO.getCreditStatus();
        if (CreditConstant.CreditStatusEnum.ACTIVE.getValue().equals(creditStatus) && updateCredit) {
            if (this.currentBalance.compareTo(amount) >= 0) {
                if (null != function) return function.apply(this, amount);
            } else {
                //余额不足扣减，使用授信额度
                if (null != function) function.apply(this, this.currentBalance);
                //把余额全部冻结 剩余需要扣除的钱
                BigDecimal needDeducted = amount.subtract(this.currentBalance);
                this.creditInfoBO.changeCreditAmount(needDeducted);
            }
        } else {
            //正常逻辑走
            if (null != function) return function.apply(this, amount);
        }
        return false;
    }

    /**
     * 充值处理
     *
     * @param amount
     * @return
     */
    public Boolean rechargeAndSetAmount(BigDecimal amount) {
        CreditInfoBO creditInfoBO = this.creditInfoBO;
        Integer creditStatus = creditInfoBO.getCreditStatus();
        if (!CreditConstant.CreditStatusEnum.NOT_ENABLED.getValue().equals(creditStatus)) {
            // 只要有授信额度 优先充值（还款）授信额度
            BigDecimal bigDecimal = creditInfoBO.rechargeCreditAmount(amount);
            recharge(bigDecimal);
            return true;
        } else {
            //正常充值
            rechargeAmount(amount);
            return true;
        }
    }

    private void rechargeAmount(BigDecimal amount) {
        this.currentBalance = this.currentBalance.add(amount);
        this.totalBalance = this.totalBalance.add(amount);
    }

    /**
     * 冻结 只扣减额度
     *
     * @param amount 扣减金额
     * @return 是否扣减成功
     */
    public boolean freeze(BigDecimal amount) {
        if (this.currentBalance.compareTo(amount) >= 0) {
            this.currentBalance = this.currentBalance.subtract(amount);
            this.freezeBalance = this.freezeBalance.add(amount);
            return true;
        }
        return false;
    }

    /**
     * 支付
     *
     * @param amount
     * @return
     */
    public Boolean pay(BigDecimal amount) {
        if (this.currentBalance.compareTo(amount) >= 0) {
            // 可用
            this.currentBalance = this.currentBalance.subtract(amount);
            // 总余额
            this.totalBalance = this.totalBalance.subtract(amount);
            return true;
        }
        return false;
    }

    /**
     * 充值
     *
     * @param amount
     * @return
     */
    private Boolean recharge(BigDecimal amount) {
        if (this.currentBalance.compareTo(amount) >= 0) {
            // 可用
            this.currentBalance = this.currentBalance.add(amount);
            // 总余额
            this.totalBalance = this.totalBalance.add(amount);
            return true;
        }
        return false;
    }
}
