package com.szmsd.common.core.language.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

import static com.szmsd.common.core.language.enums.LocalLanguageTypeEnum.*;

@Getter
@AllArgsConstructor
public enum LocalLanguageEnum {

    /** 状态：已取消 **/
    INBOUND_RECEIPT_STATUS_0(INBOUND_RECEIPT_STATUS,"0", "已取消", "Cancelled"),
    /** 状态：初始 **/
    INBOUND_RECEIPT_STATUS_1(INBOUND_RECEIPT_STATUS,"1", "初始", "Init"),
    /** 状态：已提审 **/
    INBOUND_RECEIPT_STATUS_2(INBOUND_RECEIPT_STATUS,"2", "已提审", "Arraigned"),
    /** 状态：审核通过 **/
    INBOUND_RECEIPT_STATUS_3(INBOUND_RECEIPT_STATUS,"3", "审核通过", "Review passed"),
    /** 状态：审核失败 **/
    INBOUND_RECEIPT_STATUS_3_(INBOUND_RECEIPT_STATUS, "-3", "审核失败", "Review failure"),
    /** 状态：处理中 **/
    INBOUND_RECEIPT_STATUS_4(INBOUND_RECEIPT_STATUS, "4", "处理中", "Processing"),
    /** 状态：已完成 **/
    INBOUND_RECEIPT_STATUS_5(INBOUND_RECEIPT_STATUS, "5", "已完成", "Completed"),

    INVENTORY_RECORD_TYPE_1(INVENTORY_RECORD_TYPE, "1", "入库", "Inbound inventory"),
    INVENTORY_RECORD_TYPE_2(INVENTORY_RECORD_TYPE, "2", "出库", "Outbound inventory"),
    INVENTORY_RECORD_TYPE_3(INVENTORY_RECORD_TYPE, "3", "冻结", "Freeze inventory"),
    INVENTORY_RECORD_TYPE_4(INVENTORY_RECORD_TYPE, "4", "盘点", "Check inventory"),

    /** 入库单：自动审核 **/
    INBOUND_RECEIPT_REVIEW_0(INBOUND_RECEIPT_REVIEW, "0", "自动审核", "Auto review"),
    /** 入库单：人工审核 **/
    INBOUND_RECEIPT_REVIEW_1(INBOUND_RECEIPT_REVIEW, "1", "人工审核", "Manual review"),

    /** 上架入库 **/
    INBOUND_INVENTORY_LOG(INVENTORY_RECORD_LOGS, LocalLanguageEnum.INVENTORY_RECORD_TYPE_1.getKey(), "操作人：{0}, 在{1}操作上架入库[单号: {2}, SKU：{3}, 仓库编码：{4} , 数量: {5}]", "operator: {0}, in {1} operate put inbound inventory[receiptNo: {2}, SKU：{3}, warehouseCode：{4}, quantity: {5}]"),

    /** 不需要 **/
    NEED_0(NEED, "0", "不需要", "Not needed"),
    /** 需要 **/
    NEED_1(NEED, "1", "需要", "needed"),

    /** 无效 **/
    VALID_0(NEED, "0", "无效", "Invalid"),
    /** 有效 **/
    VALID_1(NEED, "1", "有效", "Effective"),
    ;
    
    private LocalLanguageTypeEnum typeEnum;

    private String key;

    private String zhName;

    private String ehName;


    public static LocalLanguageEnum getLocalLanguageEnum(LocalLanguageTypeEnum typeEnum, String key) {
        return Stream.of(values()).filter(item -> item.getTypeEnum() == typeEnum && item.getKey().equals(key)).findFirst().orElse(null);
    }

    public static String getLocalLanguageSplice(LocalLanguageTypeEnum typeEnum, String key) {
        LocalLanguageEnum localLanguageEnum = Stream.of(values()).filter(item -> item.getTypeEnum() == typeEnum && item.getKey().equals(key)).findFirst().orElse(null);
        if (localLanguageEnum == null) {
            return "";
        }
        return localLanguageEnum.getZhName().concat("-").concat(localLanguageEnum.getEhName());
    }

    public static String getLocalLanguageSplice(LocalLanguageEnum localLanguageEnum) {
        return localLanguageEnum.getZhName().concat("-").concat(localLanguageEnum.getEhName());
    }

}
