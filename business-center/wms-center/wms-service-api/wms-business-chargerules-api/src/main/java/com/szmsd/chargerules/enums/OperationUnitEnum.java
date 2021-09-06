package com.szmsd.chargerules.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName: OperationUnitEnum
 * @Description: 计费单位枚举
 * @Author: 11
 * @Date: 2021-09-02 17:13
 */
@Getter
@AllArgsConstructor
public enum OperationUnitEnum {
    ITEM(0, "件"),
    WEIGHT(1, "重量");
    private Integer key;
    private String value;
}
