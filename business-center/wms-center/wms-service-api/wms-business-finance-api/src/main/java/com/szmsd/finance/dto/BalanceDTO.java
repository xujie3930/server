package com.szmsd.finance.dto;

import com.szmsd.finance.enums.CreditConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.function.BiFunction;

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

    /**
     * 扣钱是写日志使用
     */
    @ApiModelProperty(value = "实际扣费金额(余额扣费)")
    private BigDecimal actualDeduction;
    @ApiModelProperty(value = "使用授信金额")
    private BigDecimal creditUseAmount;

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
    public Boolean checkAndSetAmountAndCreditAnd(BigDecimal amount, boolean updateCredit, BiFunction<BalanceDTO, BigDecimal, Boolean> function) {
        CreditInfoBO creditInfoBO = this.creditInfoBO;
        Integer creditStatus = creditInfoBO.getCreditStatus();
        if (CreditConstant.CreditStatusEnum.ACTIVE.getValue().equals(creditStatus)) {
            if (this.currentBalance.compareTo(amount) >= 0) {
                this.actualDeduction = amount;
                this.creditUseAmount = BigDecimal.ZERO;
                if (null != function) return function.apply(this, amount);
            } else {
                boolean b = false;
                // 余额不足扣减，使用授信额度
                BigDecimal currentBalance = this.currentBalance;
                // 可能未负数 把余额全部冻结 剩余需要扣除的钱
                this.actualDeduction = currentBalance.min(amount).max(BigDecimal.ZERO);
                BigDecimal needDeducted = amount.subtract(currentBalance.max(BigDecimal.ZERO));
                this.creditUseAmount = needDeducted;
                if (null != function) function.apply(this, amount);
                b = this.creditInfoBO.changeCreditAmount(needDeducted, updateCredit);
                return b;
            }
        } else {
            this.actualDeduction = amount;
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
        if (!(creditStatus == null || CreditConstant.CreditStatusEnum.NOT_ENABLED.getValue().equals(creditStatus))) {
            // 只要有授信额度 优先充值（还款）授信额度
            BigDecimal bigDecimal = creditInfoBO.rechargeCreditAmount(amount);
            if (creditInfoBO.getRepaymentAmount().compareTo(BigDecimal.ZERO)>=0){
                //还清欠款

            }
            return recharge(bigDecimal);
        } else {
            //正常充值
            return recharge(amount);
        }
    }

    /**
     * 冻结 只扣减额度
     *
     * @param amount 扣减金额
     * @return 是否扣减成功
     */
    public boolean freeze(BigDecimal amount) {
        this.currentBalance = this.currentBalance.subtract(amount);
        this.freezeBalance = this.freezeBalance.add(amount);
        return BigDecimal.ZERO.compareTo(this.currentBalance) <= 0;
    }

    /**
     * 入库费扣费 扣成负数
     * @param amount
     * @return
     */
    public Boolean payAnyWay(BigDecimal amount) {
        if (this.currentBalance.compareTo(amount) >= 0) {
            // 可用
            this.currentBalance = this.currentBalance.subtract(amount);
            // 总余额
            this.totalBalance = this.totalBalance.subtract(amount);
            return true;
        } else {
            // 可用
            this.currentBalance = this.currentBalance.subtract(amount);
            // 总余额
            this.totalBalance = this.totalBalance.subtract(amount);
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
        } else {
            // 可用
            BigDecimal currentBalance = this.currentBalance;
            this.currentBalance = BigDecimal.ZERO;
            // 总余额
            this.totalBalance = this.totalBalance.subtract(currentBalance);
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
        if (amount.compareTo(BigDecimal.ZERO) >= 0) {
            // 可用
            this.currentBalance = this.currentBalance.add(amount);
            // 总余额
            this.totalBalance = this.totalBalance.add(amount);
//            creditInfoUseBO.setType(1);
//            creditInfoUseBO.getRepaymentAmount()
//            creditInfoUseBO.setRepaymentAmount();
            return true;
        }
        return false;
    }
}
