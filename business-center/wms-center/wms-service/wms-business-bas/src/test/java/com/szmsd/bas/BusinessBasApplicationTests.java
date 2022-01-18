package com.szmsd.bas;

import com.szmsd.bas.service.IBasSellerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class BusinessBasApplicationTests {
    @Resource
    private IBasSellerService iBasSellerService;

    @Test
    void contextLoads() {
        iBasSellerService.updateUserInfoForMan();
    }

}
