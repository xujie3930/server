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
    public enum InboundReceiptStatus implements InboundReceiptEnumMethods {

        /** 状态：普通入库 **/
        CANCELLED( "0", "已取消"),
        /** 状态：转运入库 **/
        TRANSFER( "1", "待提审"),
        /** 状态：采购入库 **/
        TO_BE_RECEIVED( "2", "待收货"),
        /** 状态：上架入库 **/
        WAREHOUSE_PROCESSING( "3", "仓库处理中"),
        /** 状态：点数入库 **/
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
