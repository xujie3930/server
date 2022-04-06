package com.szmsd.delivery.exported;

/**
 * @author zhangyuyuan
 * @date 2021-04-23 15:13
 */
public interface ExportContext {

    /**
     * 获取状态名称
     *
     * @param state state
     * @return String
     */
    String getStateName(String state);

    /**
     * 获取仓库名称
     *
     * @param warehouseCode warehouseCode
     * @return String
     */
    String getWarehouseName(String warehouseCode);

    /**
     * 获取出库方式名称
     *
     * @param orderType orderType
     * @return String
     */
    String getOrderTypeName(String orderType);

    /**
     * 获取异常状态名称
     *
     * @param exceptionState exceptionState
     * @return String
     */
    String getExceptionStateName(String exceptionState);

    /**
     * 获取国家名称
     *
     * @param countryCode countryCode
     * @return String
     */
    String getCountry(String countryCode);

    String len();
}
