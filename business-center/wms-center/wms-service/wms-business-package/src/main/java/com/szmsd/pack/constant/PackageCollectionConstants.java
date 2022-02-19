package com.szmsd.pack.constant;

public final class PackageCollectionConstants {

    /**
     * 揽收至仓库 - 是
     */
    public static final String COLLECTION_TO_WAREHOUSE_YES = "Y";

    /**
     * 揽收至仓库 - 否
     */
    public static final String COLLECTION_TO_WAREHOUSE_NO = "N";

    /**
     * 揽收计划 - 是
     */
    public static final String COLLECTION_PLAN_YES = "Y";

    /**
     * 揽收计划 - 否
     */
    public static final String COLLECTION_PLAN_NO = "N";

    /**
     * 处理方式 - 存储
     */
    public static final String HANDLE_MODE_STORAGE = "storage";

    /**
     * 处理方式 - 销毁
     */
    public static final String HANDLE_MODE_DESTROY = "destroy";

    /**
     * 状态
     */
    public enum Status {
        /**
         * 有计划
         */
        PLANNED,
        /**
         * 没有计划
         */
        NO_PLAN,
        /**
         * 揽收中
         */
        COLLECTING,
        /**
         * 已完成
         */
        COMPLETED,
        /**
         * 已取消
         */
        CANCELLED,
        ;
    }
}
