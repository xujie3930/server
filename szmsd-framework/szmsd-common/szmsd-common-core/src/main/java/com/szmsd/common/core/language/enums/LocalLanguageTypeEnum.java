package com.szmsd.common.core.language.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LocalLanguageTypeEnum {

    /** 系统语言 **/
    SYSTEM_LANGUAGE,

    /** 入库单状态 **/
    INBOUND_RECEIPT_STATUS,

    /** 库存日志类型 **/
    INVENTORY_RECORD_TYPE,

    /** 库存日志 **/
    INVENTORY_RECORD_LOGS,

    /** 入库单是否人工审核 **/
    INBOUND_RECEIPT_REVIEW,

    /** 是否需要 **/
    NEED,

    /** 是否有效 **/
    VALID
    ;
}
