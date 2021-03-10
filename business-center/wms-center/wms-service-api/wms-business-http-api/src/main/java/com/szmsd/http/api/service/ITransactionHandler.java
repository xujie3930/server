package com.szmsd.http.api.service;

import com.szmsd.common.core.domain.R;

/**
 * @author zhangyuyuan
 * @date 2021-03-10 9:39
 */
public interface ITransactionHandler<D, C> {

    /**
     * 获取事务数据
     *
     * @param invoiceNo   invoiceNo
     * @param invoiceType invoiceType
     * @return D
     */
    R<D> get(String invoiceNo, String invoiceType);

    /**
     * 事务执行成功后回调
     *
     * @param c           c
     * @param invoiceNo   invoiceNo
     * @param invoiceType invoiceType
     */
    void callback(C c, String invoiceNo, String invoiceType);
}
