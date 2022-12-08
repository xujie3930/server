package com.szmsd.delivery;

import com.szmsd.delivery.service.DelOutboundEmailService;
import com.szmsd.delivery.service.IDelOutboundService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BusinessDeliveryApplication.class)
public class DelOutBoundEmailTest {
    @Autowired
    private DelOutboundEmailService delOutboundEmailService;

    @Test
    public void  selectOmsWmsLog(){
        delOutboundEmailService.selectOmsWmsLog();
    }
}
