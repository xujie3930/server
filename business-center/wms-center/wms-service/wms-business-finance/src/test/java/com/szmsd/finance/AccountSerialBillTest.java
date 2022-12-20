package com.szmsd.finance;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.support.Context;
import com.szmsd.finance.domain.FssBank;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.service.AccountBillRecordService;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.IAccountSerialBillService;
import com.szmsd.finance.vo.BillBalanceVO;
import com.szmsd.finance.vo.EleBillQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BusinessFinanceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountSerialBillTest {

    @Resource
    private AccountBillRecordService accountSerialBillService;

    @Resource
    private IAccountBalanceService iAccountBalanceService;

    @Resource
    private IAccountSerialBillService iAccountSerialBillService;


    @Test
    public void asyncExport(){

        AccountSerialBillDTO dto = new AccountSerialBillDTO();
        dto.setPaymentTimeStart("2022-01-01");
        dto.setPaymentTimeEnd("2022-11-23");

        iAccountSerialBillService.asyncExport(null,dto);
    }

    @Test
    public void contextTest(){

        List<FssBank> fssBanks = new ArrayList<>();
        FssBank fssBank = new FssBank();
        fssBank.setBankCode("US1");
        fssBank.setBankName("工商");
        fssBank.setBankAccount("dds");
        fssBank.setCurrencyCode("USD");

        FssBank fssBank1 = new FssBank();
        fssBank1.setBankCode("US11");
        fssBank1.setBankName("工商1");
        fssBank1.setBankAccount("dds1");
        fssBank1.setCurrencyCode("USD");
        fssBanks.add(fssBank1);
        fssBanks.add(fssBank);

        Context.batchInsert("fss_bank",fssBanks,"id");
    }

    @Test
    public void asyncCount(){

        AccountSerialBillDTO dto = new AccountSerialBillDTO();
        dto.setPaymentTimeStart("2022-01-01");
        dto.setPaymentTimeEnd("2022-11-23");

        R<Integer> integerR = iAccountSerialBillService.exportCount(dto);

        System.out.println(JSON.toJSONString(integerR));
    }

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
