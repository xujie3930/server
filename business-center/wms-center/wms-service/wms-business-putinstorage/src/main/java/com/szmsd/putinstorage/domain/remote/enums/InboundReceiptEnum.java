package com.szmsd.putinstorage.domain.remote.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class InboundReceiptEnum {

    @Getter
    @AllArgsConstructor
    public enum OrderType {
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
        private String key;

        private String value;
    }
}
