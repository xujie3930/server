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
        /** 入库单类型：集运入库 **/
        COLLECTION("Collection", "集运入库"),
        /** 入库单类型：新SKU入库 **/
        NEW_SKU("NewSku", "新SKU入库"),
        /** 入库单类型：拆分SKU入库 **/
        SPLIT_SKU("SplitSku", "拆分SKU入库"),
        /** 入库单类型：采购入库 **/
        PURCHASE("Purchase", "采购入库"),
        /** 入库单类型：上架入库 **/
        PUTAWAY("Putaway", "上架入库"),
        /** 入库单类型：点数入库 **/
        COUNTING("Counting", "点数入库"),
        /** 入库单类型：转运入库 **/
        PACKAGE_TRANSFER("PackageTransfer", "包裹转运入库"),
        ;
        private String value;

        private String value2;
    }

    @Getter
    @AllArgsConstructor
    public enum InboundReceiptStatus implements InboundReceiptEnumMethods {
        /** 状态：已取消 **/
        CANCELLED( "0", "已取消"),
        /** 状态：初始 **/
        INIT( "1", "初始"),
        /** 状态：已提审 **/
        ARRAIGNED( "2", "已提审"),
        /** 状态：审核通过 **/
        REVIEW_PASSED( "3", "审核通过"),
        /** 状态：审核失败 **/
        REVIEW_FAILURE( "-3", "审核失败"),
        /** 状态：处理中 **/
        PROCESSING( "4", "处理中"),
        /** 状态：已完成 **/
        COMPLETED( "5", "已完成"),
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
