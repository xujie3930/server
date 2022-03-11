package com.szmsd.delivery.enums;

/**
 * @author zhangyuyuan
 * @date 2021-04-29 16:08
 */
public class DelOutboundConstant {

    /**
     * 装箱状态，0未装箱
     */
    public static final int CONTAINER_STATE_0 = 0;

    /**
     * 装箱状态，1装箱
     */
    public static final int CONTAINER_STATE_1 = 1;

    /**
     * 出库单据来源 - web界面新增
     */
    public static final String SOURCE_TYPE_ADD = "ADD";

    /**
     * 导入
     */
    public static final String SOURCE_TYPE_IMP = "IMP";

    /**
     * DOC接口
     */
    public static final String SOURCE_TYPE_DOC = "DOC";

    /**
     * Y        是重派
     * N        不是重派
     * 默认值N
     */
    public static final String REASSIGN_TYPE_Y = "Y";
    public static final String REASSIGN_TYPE_N = "N";


    public static final String SUPPLIER_CALC_TYPE_SERVICE = "LogisticsService";

    public static final String SUPPLIER_CALC_TYPE_ROUTE_ID = "LogisticsRoute";

}
