package com.szmsd.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidResult {

    /**
     * 提示消息
     */
    private String message;

    /**
     * 字段名称
     */
    private String field;

}
