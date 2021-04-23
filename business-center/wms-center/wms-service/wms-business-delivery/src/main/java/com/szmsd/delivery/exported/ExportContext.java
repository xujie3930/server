package com.szmsd.delivery.exported;

/**
 * @author zhangyuyuan
 * @date 2021-04-23 15:13
 */
public interface ExportContext {

    /**
     * 获取仓库名称
     *
     * @param warehouseCode warehouseCode
     * @return String
     */
    String getWarehouseName(String warehouseCode);

    /**
     * 获取异常状态名称
     *
     * @param exceptionState exceptionState
     * @return String
     */
    String getExceptionStateName(String exceptionState);
}
