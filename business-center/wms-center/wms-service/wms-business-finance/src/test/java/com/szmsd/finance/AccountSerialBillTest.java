package com.szmsd.finance;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.finance.service.IAccountSerialBillService;
import com.szmsd.finance.vo.BillBalanceVO;
import com.szmsd.finance.vo.EleBillQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BusinessFinanceApplication.class)
public class AccountSerialBillTest {

    @Resource
    private IAccountSerialBillService accountSerialBillService;

    @Test
    public void balancePage() {

        EleBillQueryVO queryVO = new EleBillQueryVO();
        queryVO.setBillStartTime("2022-09-25 00:00:00");
        queryVO.setBillEndTime("2022-09-26 23:59:59");

        List<BillBalanceVO> billBalanceVOS = accountSerialBillService.balancePage(queryVO);

        System.out.println(JSONObject.toJSONString(billBalanceVOS));

    }
}
