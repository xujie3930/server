package com.szmsd.common.core.language.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

import static com.szmsd.common.core.language.enums.LocalLanguageTypeEnum.*;

@Getter
@AllArgsConstructor
public enum LocalLanguageEnum {

    /** 状态：普通入库 **/
    CANCELLED(INBOUND_RECEIPT_STATUS, "0", "已取消", "Cancelled"),
    /** 状态：转运入库 **/
    TRANSFER(INBOUND_RECEIPT_STATUS, "1", "待提审", "Pending"),
    /** 状态：采购入库 **/
    TO_BE_RECEIVED(INBOUND_RECEIPT_STATUS, "2", "待收货", "To be received"),
    /** 状态：上架入库 **/
    WAREHOUSE_PROCESSING(INBOUND_RECEIPT_STATUS, "3", "仓库处理中", "Warehouse processing"),
    /** 状态：点数入库 **/
    HAS_BEEN_STORED(INBOUND_RECEIPT_STATUS, "4", "已入库", "Has been stored"),

    INVENTORY_RECORD_TYPE_1(INVENTORY_RECORD_TYPE, "1", "入库", "Inbound inventory"),
    INVENTORY_RECORD_TYPE_2(INVENTORY_RECORD_TYPE, "2", "出库", "Outbound inventory"),
    INVENTORY_RECORD_TYPE_3(INVENTORY_RECORD_TYPE, "3", "冻结", "Freeze inventory"),

    /** 上架入库 **/
    INBOUND_INVENTORY_LOG(INVENTORY_RECORD_LOGS, LocalLanguageEnum.INVENTORY_RECORD_TYPE_1.getKey(), "操作人：{0}, 在{1}操作上架入库[单号: {2}, SKU：{3}, 仓库编码：{4} , 数量: {5}]", "operator: {0}, in {1} operate put inbound inventory[receiptNo: {2}, SKU：{3}, warehouseCode：{4}, quantity: {5}]"),
    ;
    
    private LocalLanguageTypeEnum typeEnum;

    private String key;

    private String zhName;

    private String ehName;


    public static LocalLanguageEnum getLocalLanguageEnum(LocalLanguageTypeEnum typeEnum, String key) {
        return Stream.of(values()).filter(item -> item.getTypeEnum() == typeEnum && item.getKey().equals(key)).findFirst().orElse(null);
    }

}
