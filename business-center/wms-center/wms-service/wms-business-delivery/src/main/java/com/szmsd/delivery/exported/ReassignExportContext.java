package com.szmsd.delivery.exported;

/**
 * @author zhangyuyuan
 * @date 2021-04-23 15:13
 */
public interface ReassignExportContext {

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
     * 获取轨迹状态名称
     *
     * @param trackingStatus trackingStatus
     * @return String
     */
    String getTrackingStatusName(String trackingStatus);

    /**
     * 获取国家名称
     *
     * @param countryCode countryCode
     * @return String
     */
    String getCountry(String countryCode);

    String len();
}
