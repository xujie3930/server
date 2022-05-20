package com.szmsd.ec.constant;

/**
 *
 */
public class OrderStatusConstant {

    /**
     * 未发货
     */
    public static final String UNSHIPPED ="UnShipped";
    /**
     * 已发货
     */
    public static final String SHIPPED ="Shipped";
    /**
     * 异常
     */
    public static final String EXCEPTION ="Exception";
    /**
     * 出库中
     */
    public static final String DELIVERING ="Delivering";
    /**
     * 完成
     */
    public static final String COMPELETED ="Compeleted";

    /**
     * 取消
     */
    public static final String CANCEL ="Canceled";

    /**
     * 删除
     */
    public static final String DELETED ="Deleted";
    /**
     * 平台状态
     */
    public class OrderPlatformStatusConstant {

        public static final String APPROVED = "Approved";

        public static final String PENDING_APPROVED = "Pending Approved";

        public static final String CANCELLED = "Cancelled";
    }
}
