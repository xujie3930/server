package com.szmsd.putinstorage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum InboundReceiptRecordEnum {

    CREATE("创建", "warehouseNo", "", "创建", null, "", "",
            (param) -> InboundReceiptRecordEnum.valueOf("CREATE").getContent()),

    ARRAIGNED("提审", "", "", "提审", null, "", "",
            (param) -> InboundReceiptRecordEnum.valueOf("ARRAIGNED").getContent()),

    CANCEL("取消", "", "", "取消", null, "", "",
            (param) -> InboundReceiptRecordEnum.valueOf("CANCEL").getContent()),

    REVIEW("审核", "warehouseNos", "", "审核", null, "", "",
            (param) -> InboundReceiptRecordEnum.valueOf("REVIEW").getContent()),

    PUT("上架", "orderNo", "sku", "SKU[{0}]，上架[{1}]", Arrays.asList("sku", "qty"), "operator", "operateOn",
            (param) -> CollectionUtils.isEmpty(param) ? InboundReceiptRecordEnum.valueOf("PUT").getContent() : MessageFormat.format(InboundReceiptRecordEnum.valueOf("PUT").getContent(), param.toArray())),

    COMPLETED("完成", "orderNo", "", "完成", null, "operator", "operateOn",
            (param) -> InboundReceiptRecordEnum.valueOf("COMPLETED").getContent()),
    ;

    /** 创建，提审，取消，审核，上架，完成 **/
    private final String type;

    private final String warehouseNo;

    private final String sku;

    /** 内容 **/
    private final String content;

    private final List<String> contentFill;

    private final String createBy;

    private final String createByName;

    /** 自定义表达式 **/
    private final Function<List<String>, String> func;

    public String get(List<String> param) {
        return func.apply(param);
    }

}