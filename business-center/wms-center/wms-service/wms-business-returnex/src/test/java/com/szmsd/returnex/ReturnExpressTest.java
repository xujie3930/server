package com.szmsd.returnex;

import com.szmsd.returnex.service.IReturnExpressService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Import({ FeignAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class })
@SpringBootTest(classes = BusinessReturnExpressApplication.class)
public class ReturnExpressTest {

    @Autowired
    private IReturnExpressService expressService;

    @Test
    public void autoGeneratorFee(){
        expressService.autoGeneratorFee();
    }
}
