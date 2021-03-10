package com.szmsd.http.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.service.ITransactionHandler;
import org.springframework.stereotype.Component;

/**
 * @author zhangyuyuan
 * @date 2021-03-10 10:17
 */
@Component
public class Test1ITransactionHandler implements ITransactionHandler<String, String> {

    @Override
    public R<String> get(String invoiceNo, String invoiceType) {
        return null;
    }

    @Override
    public void callback(String s, String invoiceNo, String invoiceType) {

    }
}
