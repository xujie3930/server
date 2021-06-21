package com.szmsd.putinstorage.enums;

import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import com.szmsd.common.core.language.util.LanguageUtil;
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

    CREATE("创建", "warehouseNo", "", "创建", null, "", "", "",
            (param) -> InboundReceiptRecordEnum.valueOf("CREATE").getContent()),

    ARRAIGNED("提审", "", "", "提审", null, "", "", "",
            (param) -> InboundReceiptRecordEnum.valueOf("ARRAIGNED").getContent()),

    CANCEL("取消", "", "", "取消", null, "", "", "",
            (param) -> InboundReceiptRecordEnum.valueOf("CANCEL").getContent()),

    REVIEW("审核", "warehouseNos", "", "{0}，{1}", Arrays.asList("status", "reviewRemark"), "", "", "",
            (param) -> {
                if (CollectionUtils.isEmpty(param)) {
                    return "";
                }
                String arg0 = LanguageUtil.getLanguage(LocalLanguageTypeEnum.INBOUND_RECEIPT_STATUS, param.get(0));
                String arg1 = param.get(1);
                return MessageFormat.format(InboundReceiptRecordEnum.valueOf("REVIEW").getContent(), arg0, arg1);
            }),

    PUT("上架", "orderNo", "sku", "SKU[{0}]，上架数量[{1}]", Arrays.asList("sku", "qty"), "operator", "operator", "operateOn",
            (param) -> CollectionUtils.isEmpty(param) ? InboundReceiptRecordEnum.valueOf("PUT").getContent() : MessageFormat.format(InboundReceiptRecordEnum.valueOf("PUT").getContent(), param.toArray())),

    COMPLETED("完成", "orderNo", "", "完成", null, "operator", "operator", "operateOn",
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

    private final String createTime;

    /** 自定义表达式 **/
    private final Function<List<String>, String> func;

    public String get(List<String> param) {
        return func.apply(param);
    }

}