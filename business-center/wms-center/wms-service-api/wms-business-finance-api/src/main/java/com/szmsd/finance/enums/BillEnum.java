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
        EXCHANGE("03");
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
            return null;
        }
    }

    public enum PayMethod implements IEnum<String> {
        /**
         * 线下充值
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
         * 汇率转换扣款
         */
        WITHDRAW_PAYMENT("05","提现");


        @EnumValue
        private String paymentType;

        private String value;

        PayMethod(String paymentType,String value) {
            this.paymentType = paymentType;
            this.value = value;
        }

        @Override
        public String getValue() {
            return this.value;
        }

        public String getPaymentType() {
            return this.paymentType;
        }
    }
}
