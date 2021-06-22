package com.szmsd.delivery.event;

/**
 * @author zhangyuyuan
 * @date 2021-06-22 13:57
 */
public interface OperationLogEnum {

    /**
     * 获取单据号
     *
     * @param object object
     * @return String
     */
    String getInvoiceNo(Object object);

    /**
     * 获取单据类型
     *
     * @param object object
     * @return String
     */
    String getInvoiceType(Object object);

    /**
     * 获取操作类型
     *
     * @return String
     */
    String getType();

    /**
     * 获取日志
     *
     * @param object object
     * @return String
     */
    String getLog(Object object);
}
