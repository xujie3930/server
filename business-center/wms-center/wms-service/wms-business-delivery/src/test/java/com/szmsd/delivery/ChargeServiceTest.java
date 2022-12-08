package com.szmsd.delivery;

import com.szmsd.delivery.service.ChargeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Import({ FeignAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class })
@ComponentScan(value = "com.szmsd.finance.api.feign.factory")
@SpringBootTest(classes = BusinessDeliveryApplication.class)
public class ChargeServiceTest {

    @Autowired
    private ChargeService chargeService;

    @Test
    public void doSecondChargeTest(){
        chargeService.doSecondCharge();
    }
}
