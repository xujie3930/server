package com.szmsd.finance.compont;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.IDeductionRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: CreditSec
 * @Description: 授信情况还款账单定时任务
 * @Author: 11
 * @Date: 2021-10-25 11:14
 */
@Slf4j
@Component
public class CreditTask {

    @Resource
    private IAccountBalanceService iAccountBalanceService;
    @Resource
    private IDeductionRecordService iDeductionRecordService;

    /**
     * 每天0点以后去截断用户的账单
     */
//    @Scheduled(cron = "* * * * * ?")
    @Scheduled(cron = "0 10 0 * * ?")
    public void genBillTask() {
        // 查询修改授信期更新的用户列表 并更新标识
        List<AccountBalance> updateBillList = iAccountBalanceService.queryAndUpdateUserCreditTimeFlag();
        log.info("需要截断用户的数据-{}条-{}", updateBillList.size(), JSONObject.toJSONString(updateBillList));
        log.info("---截断账单---start");
        // 截断在账期结束前 的账单
        List<String> updateCusCodeList = updateBillList.stream().map(AccountBalance::getCusCode).collect(Collectors.toList());
        iDeductionRecordService.updateDeductionRecordStatus(updateCusCodeList);

        log.info("---正常结账单---start");
        // 按周期更新正常的账单
        iDeductionRecordService.updateRecordStatusByCreditTimeInterval();

        // 更新这些账户的账期
        iAccountBalanceService.updateUserCreditTime();
    }

    /**
     * 每天0点以后去迁移已还清的账单数据
     */
//    @Scheduled(cron = "* * * * * ?")
    @Scheduled(cron = "0 30 1 * * ?")
    public void moveInvalidCreditBill() {
        Long cny = 0L;
        long start = System.currentTimeMillis();
        log.info("【迁移数据】账单-开始----------");
        while ((cny = iDeductionRecordService.moveInvalidCreditBill()) > 0) {
            log.info("【迁移数据】 sync data From fss_deduction_record To fss_deduction_record_bak | count: {}", cny);
        }
        log.info("【迁移数据】账单-完成----------{}", System.currentTimeMillis() - start);
    }
}
