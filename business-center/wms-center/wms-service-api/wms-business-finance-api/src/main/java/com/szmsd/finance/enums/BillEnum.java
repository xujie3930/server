package com.szmsd.finance.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author liulei
 */
public class BillEnum implements Serializable {
    /**
     * 收入("01"),
     * 付款("02"),
     * 兑换("03"),
     * 冻结("04"),
     * 解冻结("05"),
     * 退费("06"),
     */
    public enum PayType implements IEnum<String> {
        INCOME("01"),
        PAYMENT("02"),
        EXCHANGE("03"),
        FREEZE("04"),
        PAYMENT_NO_FREEZE("05"),
        REFUND("06"),
        ;

        PayType(String payType) {
            this.payType = payType;
        }

        @EnumValue
        private String payType;

        public String getPayType() {
            return this.payType;
        }

        @Override
        public String getValue() {
            return payType;
        }
    }

    public enum PayMethod implements IEnum<String> {
        /**
         * 在线充值
         */
        ONLINE_INCOME("01", "在线充值"),
        /**
         * 线下充值
         */
        OFFLINE_INCOME("02", "线下充值"),
        /**
         * 汇率转换充值
         */
        EXCHANGE_INCOME("03", "汇率转换充值"),
        /**
         * 汇率转换扣款
         */
        EXCHANGE_PAYMENT("04", "汇率转换扣款"),
        /**
         * 提现
         */
        WITHDRAW_PAYMENT("05", "提现"),
        /**
         * 特殊操作
         */
        SPECIAL_OPERATE("06", "特殊操作"),
        /**
         * 业务操作
         */
        BUSINESS_OPERATE("07", "业务操作"),
        /**
         * 仓租
         */
        WAREHOUSE_RENT("08", "仓租"),
        /**
         * 余额冻结
         */
        BALANCE_FREEZE("09", "余额冻结"),
        /**
         * 余额解冻
         */
        BALANCE_THAW("10", "余额解冻"),
        /**
         * 费用扣除
         */
        BALANCE_DEDUCTIONS("11", "费用扣除"),
        /**
         * 余额转换
         */
        BALANCE_EXCHANGE("12", "余额转换"),
        /**
         * 退费
         */
        REFUND("13", "退费"),
        ;


        @EnumValue
        private String paymentType;

        private String paymentName;

        PayMethod(String paymentType, String paymentName) {
            this.paymentType = paymentType;
            this.paymentName = paymentName;
        }

        @Override
        public String getValue() {
            return this.paymentType;
        }

        public String getPaymentType() {
            return this.paymentType;
        }

        public String getPaymentName() {
            return this.paymentName;
        }
    }

    /**
     * 性质 费用扣除、补收、优惠、退费、赔付、人工充值、线上充值
     */
    @Getter
    @AllArgsConstructor
    public enum NatureEnum {
        /**
         * 费用扣除
         */
        EXPENSE_DEDUCTION("费用扣除"),
        /**
         * 补收
         */
        MAKE_UP("补收"),
        /**
         * 优惠
         */
        PREFERENTIAL("优惠"),
        /**
         * 退费
         */
        REFUND("退费"),
        /**
         * 赔付
         */
        PAY_DAMAGES("赔付"),
        /**
         * 人工充值
         */
        MANUAL_RECHARGE("人工充值"),
        /**
         * 线上充值
         */
        ONLINE_RECHARGE("线上充值"),
        ;
        private final String name;
    }

    /**
     * 产品类别 （普通出库单、自提出库单、销毁出库单、集运出库单等）、充值、仓租
     */
    @Getter
    @AllArgsConstructor
    public enum ProductCategoryEnum {
        /**
         * 订单类型（普通出库单、自提出库单、销毁出库单、集运出库单等）、充值、仓租
         */
        NORMAL("Normal", "普通出库单"),
        DESTROY("Destroy", "销毁出库单"),
        SELF_PICK("SelfPick", "自提出库单"),
        PACKAGE_TRANSFER("PackageTransfer", "转运出库单"),
        COLLECTION("Collection", "集运出库单"),
        NEW_SKU("NewSku", "组合SKU上架出库单"),
        SPLIT_SKU("SplitSku", "拆分SKU上架出库单"),
        BATCH("Batch", "批量出库单"),
        SALES("Sales", "普通销售订单"),
        RECHARGE("Recharge", "充值"),
        WAREHOUSE_RENTAL("WarehouseRental", "仓租"),
        ;
        private final String code;
        private final String name;

    }

    /**
     * 费用类别 仓库操作费、包材费、物流运费、税金、仓租
     */
    @Getter
    @AllArgsConstructor
    public enum CostCategoryEnum {
        /**
         * 仓库操作费、包材费、物流运费、税金、仓租
         */
        WAREHOUSE_OPERATION_FEE("仓库操作费"),
        PACKING_MATERIAL_FEE("包材费"),
        LOGISTICS_FREIGHT("物流运费"),
        TAXES("税金"),
        WAREHOUSE_RENTAL("仓租"),
        SPECIAL_OPERATING_FEE("特殊操作费"),
        OPERATING_FEE("操作费"),
        MATERIAL_COST("物料费"),
        REFUND("退费"),
        ;
        private final String name;
    }

    /**
     * 费用类型为：出库操作费、物流基础运费、物流操作费、物流处理费、PRC返回其它费用
     */
    @Getter
    @AllArgsConstructor
    public enum FeeTypeEnum {
        /**
         * 费用类型为：出库操作费、物流基础运费、物流操作费、物流处理费、PRC返回其它费用
         */
        OUTBOUND_OPERATION_FEE("出库操作费"),
        LOGISTICS_BASIC_FREIGHT("物流基础运费"),
        LOGISTICS_OPERATION_FEE("物流操作费"),
        LOGISTICS_PROCESSING_FEE("物流处理费"),
        BALANCE_DEDUCTIONS("费用扣除"),
        ;
        private final String name;
    }

}
