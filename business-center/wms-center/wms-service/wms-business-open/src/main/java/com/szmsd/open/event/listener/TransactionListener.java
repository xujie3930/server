package com.szmsd.open.event.listener;

import com.szmsd.open.event.TransactionEvent;
import com.szmsd.open.service.IOpnTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author zhangyuyuan
 * @date 2021-03-08 14:51
 */
@Component
public class TransactionListener {

    @Autowired
    private IOpnTransactionService opnTransactionService;

    @Async
    @EventListener
    public void onApplicationEvent(TransactionEvent event) {
        if (null != event.getSource()) {
//            this.opnTransactionService.onRep((String) event.getSource());
        }
    }
}
