package com.szmsd.putinstorage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 提单枚举
 * @author MSD
 * @date 2021-01-07
 */
public class InboundReceiptEnum {

    @Getter
    @AllArgsConstructor
    public enum OrderType implements InboundReceiptEnumMethods {
        /** 入库单类型：普通入库 **/
        NORMAL("Normal", "普通入库"),
        /** 入库单类型：转运入库 **/
        TRANSFER("Transfer", "转运入库"),
        /** 入库单类型：采购入库 **/
        PURCHASE("Purchase", "采购入库"),
        /** 入库单类型：上架入库 **/
        PUTAWAY("Putaway", "上架入库"),
        /** 入库单类型：点数入库 **/
        COUNTING("Counting", "点数入库"),
        ;
        private String value;

        private String value2;
    }

    @Getter
    @AllArgsConstructor
    public enum InboundReceiptStatus implements InboundReceiptEnumMethods {

        /** 状态：已取消 **/
        CANCELLED( "0", "已取消"),
        /** 状态：待提审 **/
        TRANSFER( "1", "待提审"),
        /** 状态：待收货 **/
        TO_BE_RECEIVED( "2", "待收货"),
        /** 状态：仓库处理中 **/
        WAREHOUSE_PROCESSING( "3", "仓库处理中"),
        /** 状态：已入库 **/
        HAS_BEEN_STORED( "4", "已入库"),
        ;
        private String value;
        private String value2;
    }

    public interface InboundReceiptEnumMethods {
        String getValue();
        String getValue2();

        static <E extends InboundReceiptEnumMethods> String getValue2(Class<E> enumClass, String value) {
            for (E e : enumClass.getEnumConstants()) {
                if (e.getValue().equals(value)) {
                    return e.getValue2();
                }
            }
            return "";
        }

        static <E extends InboundReceiptEnumMethods> InboundReceiptEnumMethods getEnum(Class<E> enumClass, String value) {
            for (E e : enumClass.getEnumConstants()) {
                if (e.getValue().equals(value)) {
                    return e;
                }
            }
            return null;
        }

    }
}
