package com.szmsd.delivery.timer;

import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.service.wrapper.IDelOutboundAsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DelOutboundTimerAsyncTaskAdapter {

    @Autowired
    private IDelOutboundAsyncService delOutboundAsyncService;
    @Autowired
    private DelOutboundTimerAsyncTask delOutboundTimerAsyncTask;

    public void asyncBringVerify(String orderNo, Long id) {
        String reassignType = delOutboundAsyncService.getReassignType(orderNo);
        if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(reassignType)) {
            // 重派出库单
            this.delOutboundTimerAsyncTask.asyncBringVerify2(orderNo, id);
        } else {
            // 正常的出库单
            this.delOutboundTimerAsyncTask.asyncBringVerify(orderNo, id);
        }
    }
}
