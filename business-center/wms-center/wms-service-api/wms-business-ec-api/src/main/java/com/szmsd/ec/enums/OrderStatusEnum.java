package com.szmsd.ec.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {

    /**
     * 未发货
     */
    UnShipped,

    /**
     * 已发货
     */
    Shipped,

    /**
     * 异常
     */
    Exception,

    /**
     * 出库中
     */
    Delivering,

    /**
     * 完成
     */
    Compeleted,

    /**
     * 已删除
     */
    Deleted;
}
