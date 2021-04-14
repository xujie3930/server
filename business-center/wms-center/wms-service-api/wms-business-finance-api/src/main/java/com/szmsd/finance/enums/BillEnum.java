package com.szmsd.finance.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

/**
 * @author liulei
 */
public class BillEnum implements Serializable {

    public enum PayType implements IEnum<String> {
        INCOME("01"),
        PAYMENT("02"),
        EXCHANGE("03"),
        FREEZE("04"),
        ;
        PayType(String payType){
            this.payType=payType;
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
        ONLINE_INCOME("01","在线充值"),
        /**
         * 线下充值
         */
        OFFLINE_INCOME("02","线下充值"),
        /**
         * 汇率转换充值
         */
        EXCHANGE_INCOME("03","汇率转换充值"),
        /**
         * 汇率转换扣款
         */
        EXCHANGE_PAYMENT("04","汇率转换扣款"),
        /**
         * 提现
         */
        WITHDRAW_PAYMENT("05","提现"),
        /**
         * 特殊操作
         */
        SPECIAL_OPERATE("06","特殊操作"),
        /**
         * 业务操作
         */
        BUSINESS_OPERATE("07","业务操作"),
        /**
         * 仓租
         */
        WAREHOUSE_RENT("08","仓租"),
        /**
         * 余额冻结
         */
        BALANCE_FREEZE("09","余额冻结"),
        /**
         * 余额解冻
         */
        BALANCE_THAW("10","余额解冻"),
        /**
         * 费用扣除
         */
        BALANCE_DEDUCTIONS("11","费用扣除"),
        ;


        @EnumValue
        private String paymentType;

        private String paymentName;

        PayMethod(String paymentType,String paymentName) {
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
}
