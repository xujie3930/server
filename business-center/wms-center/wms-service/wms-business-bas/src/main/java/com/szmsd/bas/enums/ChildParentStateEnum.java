package com.szmsd.bas.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态
 *
 * @author: taoJie
 */
@Getter
@AllArgsConstructor
public enum ChildParentStateEnum {

    /**
     * 审核中
     **/
    reviewing("1", "审核中"),

    /**
     * 驳回
     **/
    reject("2", "驳回"),

    /**
     * 已绑定
     **/
    confirm("3", "已绑定"),

    /**
     * 解绑
     **/
    unbind("4", "解绑"),

    /**
     * 删除
     **/
    remove("5", "删除");

    private final String key;
    private final String desc;


    /**
     * 通过key获取对象
     *
     * @param key
     * @return
     */
    public static ChildParentStateEnum fromKey(String key) {
        for (ChildParentStateEnum e : ChildParentStateEnum.values()) {
            if (e.getKey().equalsIgnoreCase(key)) {
                return e;
            }
        }
        return null;
    }


    public Integer getIntegerKey() {
        return Integer.valueOf(key);
    }
}
