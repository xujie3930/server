package com.szmsd.http.api.controller;

import com.szmsd.http.api.service.ITransactionHandler;

import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-03-10 10:00
 */
public class TransactionHandlerManager {

    private Map<String, ITransactionHandler<?, ?>> handlerMap;

    private TransactionHandlerManager() {
    }

    public static TransactionHandlerManager getInstance() {
        return TransactionHandlerManagerInstance.transactionHandlerManager;
    }

    public void refresh(Map<String, ITransactionHandler<?, ?>> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public ITransactionHandler<?, ?> get(String invoiceType) {
        if (null == this.handlerMap) {
            return null;
        }
        return this.handlerMap.get(invoiceType);
    }

    private static class TransactionHandlerManagerInstance {
        private static final TransactionHandlerManager transactionHandlerManager = new TransactionHandlerManager();
    }
}
