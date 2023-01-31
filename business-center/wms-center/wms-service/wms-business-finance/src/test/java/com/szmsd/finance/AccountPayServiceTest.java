package com.szmsd.finance;

import com.szmsd.finance.domain.AccountPay;
import com.szmsd.finance.mapper.AccountPayMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BusinessFinanceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountPayServiceTest {

    @Autowired
    private AccountPayMapper payMapper;

    @Test
    public void save(){

        AccountPay accountPay = new AccountPay();
        accountPay.setProductCode("aaaa");
        accountPay.setOrderNo("A123123");
        accountPay.setCusCode("aaaa");
        accountPay.setAmount(new BigDecimal("123.124521421"));
        accountPay.setActurlAmount(new BigDecimal("12.3214213"));
        accountPay.setProcedureAmount(new BigDecimal("123.421521342"));
        accountPay.setCallbackNumber(1L);
        accountPay.setCreateDate(new Date());
        payMapper.insert(accountPay);
    }

}
