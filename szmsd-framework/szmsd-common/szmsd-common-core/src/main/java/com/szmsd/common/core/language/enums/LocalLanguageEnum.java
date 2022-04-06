package com.szmsd.common.core.language.enums;

import com.szmsd.common.core.utils.ServletUtils;
import com.szmsd.common.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
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
    INBOUND_RECEIPT_STATUS_3(INBOUND_RECEIPT_STATUS,"3", "审核通过", "Approved"),
    /** 状态：审核失败 **/
    INBOUND_RECEIPT_STATUS_3_(INBOUND_RECEIPT_STATUS, "-3", "审核失败", "Rejected"),
    /** 状态：处理中 **/
    INBOUND_RECEIPT_STATUS_4(INBOUND_RECEIPT_STATUS, "4", "处理中", "Processing"),
    /** 状态：已完成 **/
    INBOUND_RECEIPT_STATUS_5(INBOUND_RECEIPT_STATUS, "5", "已完成", "Completed"),

    INVENTORY_RECORD_TYPE_1(INVENTORY_RECORD_TYPE, "1", "入库", "Inbound inventory"),
    INVENTORY_RECORD_TYPE_2(INVENTORY_RECORD_TYPE, "2", "出库", "Outbound inventory"),
    INVENTORY_RECORD_TYPE_3(INVENTORY_RECORD_TYPE, "3", "冻结", "Freeze inventory"),
    INVENTORY_RECORD_TYPE_8(INVENTORY_RECORD_TYPE, "8", "解冻", "unFreeze inventory"),
    INVENTORY_RECORD_TYPE_4(INVENTORY_RECORD_TYPE, "4", "盘点", "Check inventory"),
    INVENTORY_RECORD_TYPE_5(INVENTORY_RECORD_TYPE, "5", "调增", "Increase"),
    INVENTORY_RECORD_TYPE_6(INVENTORY_RECORD_TYPE, "6", "调减", "Reduce"),
    INVENTORY_RECORD_TYPE_7(INVENTORY_RECORD_TYPE, "7", "退件上架", "Return express and shelve"),

    /** 入库单：自动审核 **/
    INBOUND_RECEIPT_REVIEW_0(INBOUND_RECEIPT_REVIEW, "0", "自动审核", "Auto review"),
    /** 入库单：人工审核 **/
    INBOUND_RECEIPT_REVIEW_1(INBOUND_RECEIPT_REVIEW, "1", "人工审核", "Manual review"),

    /** 上架入库 **/
    INBOUND_INVENTORY_LOG(INVENTORY_RECORD_LOGS, LocalLanguageEnum.INVENTORY_RECORD_TYPE_1.getKey(), "操作人：{0}, 在{1}操作上架入库[单号: {2}, SKU：{3}, 仓库编码：{4} , 数量: {5}]", "operator: {0}, in {1} operate put inbound inventory[receiptNo: {2}, SKU：{3}, warehouseCode：{4}, quantity: {5}]"),

    /** 不需要 **/
    NEED_0(NEED, "0", "不需要", "Not needed"),
    /** 需要 **/
    NEED_1(NEED, "1", "需要", "Needed"),

    /** 无效 **/
    VALID_0(VALID, "0", "无效", "Invalid"),
    /** 有效 **/
    VALID_1(VALID, "1", "有效", "Effective"),

    /** 是 **/
    YN_Y(YN, "0", "否", "N"),
    /** 否 **/
    YN_N(YN, "1", "是", "Y"),

    HOME_DOCUMENT_TYPE_1(HOME_DOCUMENT_TYPE, "当天提审量", "当天提审量", "Order Submited"),
    HOME_DOCUMENT_TYPE_2(HOME_DOCUMENT_TYPE, "当天到仓量", "当天到仓量", "Received Quantity"),
    HOME_DOCUMENT_TYPE_3(HOME_DOCUMENT_TYPE, "当天装运包裹到仓量", "当天装运包裹到仓量", "Order Completed"),
    HOME_DOCUMENT_TYPE_4(HOME_DOCUMENT_TYPE, "当天出库量", "当天出库量", "Order Shipped"),
    HOME_DOCUMENT_TYPE_5(HOME_DOCUMENT_TYPE, "未处理问题件", "未处理问题件", "Untreated Problem Pieces"),

    /** 已创建订单 **/
    HOME_BAR_CHART_TYPE_1(HOME_BAR_CHART_TYPE, "已创建订单", "已创建订单", "Order Created"),
    /** 已提审订单 **/
    HOME_BAR_CHART_TYPE_2(HOME_BAR_CHART_TYPE, "已提审订单", "已提审订单", "Order Submited"),
    /** 已入库订单 **/
    HOME_BAR_CHART_TYPE_3(HOME_BAR_CHART_TYPE, "已入库订单", "已入库订单", "Order Completed"),
    /** 已出库订单 **/
    HOME_BAR_CHART_TYPE_4(HOME_BAR_CHART_TYPE, "已出库订单", "已出库订单", "Order Shipped"),

    /** 交货管理 **/
    ADDR_EXPORTED(ADDRESS_MANAGEMENT,"已导出","已导出","Exported"),
    ADDR_NOT_EXPORTED(ADDRESS_MANAGEMENT,"未导出","未导出","Not exported"),

    /** 退件 **/
    RETURN_EXPRESS_1(RETURN_EXPRESS,"wms退件通知","wms退件通知","WMS return notification"),
    RETURN_EXPRESS_2(RETURN_EXPRESS,"WMS退件通知","WMS退件通知","WMS return notification"),
    /**处理方式*/
    RETURN_EXPRESS_3(RETURN_EXPRESS,"退件预报","退件预报","Return forecast"),
    RETURN_EXPRESS_4(RETURN_EXPRESS,"销毁","销毁","Destruction"),
    RETURN_EXPRESS_5(RETURN_EXPRESS,"整包上架","整包上架","Package put for sell"),
    RETURN_EXPRESS_6(RETURN_EXPRESS,"拆包检查","拆包检查","Unpacking inspection"),
    RETURN_EXPRESS_7(RETURN_EXPRESS,"按明细上架","按明细上架","Package for sell according to details"),
    /** 退件类型 */
    RETURN_EXPRESS_8(RETURN_EXPRESS,"自有库存退件","自有库存退件","Own package inventory return"),
    RETURN_EXPRESS_9(RETURN_EXPRESS,"转动单退件","转动单退件","Rotation single package return"),
    RETURN_EXPRESS_10(RETURN_EXPRESS,"外部渠道退件","外部渠道退件","External channel package return"),
    /** 处理状态*/
    RETURN_EXPRESS_11(RETURN_EXPRESS,"处理中","处理中","In process"),
    RETURN_EXPRESS_12(RETURN_EXPRESS,"待客户处理","待客户处理","Waiting for customer to handle"),
    RETURN_EXPRESS_13(RETURN_EXPRESS,"待指派","待指派","Waiting assigned"),
    RETURN_EXPRESS_14(RETURN_EXPRESS,"已提交处理意见","已提交处理意见","Processing comments Have been submitted"),

    ;

    private LocalLanguageTypeEnum typeEnum;

    private String key;

    private String zhName;

    private String ehName;


    public String getValueLen() {
        if ("enName".equals(getLen())) {
            return this.ehName;
        }
        return this.zhName;
    }


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

    public static List<String> getLocalLanguageSplice(LocalLanguageTypeEnum typeEnum) {
        List<LocalLanguageEnum> collect = Stream.of(values()).filter(item -> item.getTypeEnum() == typeEnum).collect(Collectors.toList());
        if ("enName".equals(getLen())) {
            return collect.stream().map(LocalLanguageEnum::getEhName).collect(Collectors.toList());
        }
        return collect.stream().map(LocalLanguageEnum::getZhName).collect(Collectors.toList());
    }

    private static String getLen() {
        String len = ServletUtils.getHeaders("Langr");
        if (StringUtils.isEmpty(len)) {
            len = "zh";
        }
        return len.trim().toLowerCase().concat("Name");
    }

}
