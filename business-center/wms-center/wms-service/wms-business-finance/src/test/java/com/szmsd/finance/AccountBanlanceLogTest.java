package com.szmsd.finance;

import com.szmsd.finance.service.IAccountBalanceLogService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BusinessFinanceApplication.class)
public class AccountBanlanceLogTest {

    @Autowired
    private IAccountBalanceLogService accountBalanceLogService;

    @Test
    public void autoInsert() {
        accountBalanceLogService.autoGeneratorBalance();
    }

}
