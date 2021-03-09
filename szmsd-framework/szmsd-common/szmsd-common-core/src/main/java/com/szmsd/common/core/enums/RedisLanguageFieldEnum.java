package com.szmsd.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 多语言累加
 */
@Getter
@AllArgsConstructor
public enum RedisLanguageFieldEnum {
    // 创建/更新仓库
    ADD_WAREHOUSE_REQUEST("bas_warehouse", "warehouseCode", "warehouseNameCn-zhName,warehouseNameEn-enName"),
    ;

    /**
     * 表
     */
    private String table;

    /**
     * redis数据键
     */
    private String key;

    /**
     * 语言字段 实体field-什么语言
     */
    private String languageName;
}
