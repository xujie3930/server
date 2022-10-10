package com.szmsd.finance;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.domain.R;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.service.AccountBillRecordService;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.vo.BillBalanceVO;
import com.szmsd.finance.vo.EleBillQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BusinessFinanceApplication.class)
public class AccountSerialBillTest {

    @Resource
    private AccountBillRecordService accountSerialBillService;

    @Resource
    private IAccountBalanceService iAccountBalanceService;


    @Test
    public void balancePage() {

        EleBillQueryVO queryVO = new EleBillQueryVO();
        queryVO.setBillStartTime("2022-10-09");
        queryVO.setBillEndTime("2022-10-10");

        List<BillBalanceVO> billBalanceVOS = accountSerialBillService.balancePage(queryVO);

        System.out.println(JSON.toJSONString(billBalanceVOS));

    }

    @Test
    public void balanceExchange(){

        CustPayDTO dto = new CustPayDTO();
        dto.setRate(new BigDecimal("8"));
        dto.setAmount(new BigDecimal("11"));
        dto.setCurrencyCode("CNY");
        dto.setCurrencyName("人民币");
        dto.setCurrencyCode2("USD");
        dto.setCurrencyName2("美元");
        dto.setCusCode("CNY373");

        R r = iAccountBalanceService.balanceExchange(dto);

        System.out.println(JSON.toJSONString(r.getData()));

    }
}
