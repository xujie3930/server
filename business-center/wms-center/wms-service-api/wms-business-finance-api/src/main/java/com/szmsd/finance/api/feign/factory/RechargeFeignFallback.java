package com.szmsd.finance.api.feign.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.dto.*;
import com.szmsd.finance.vo.UserCreditInfoVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author liulei
 */
@Component
@Slf4j
public class RechargeFeignFallback implements FallbackFactory<RechargesFeignService> {

    @Override
    public RechargesFeignService create(Throwable throwable) {
        log.info("RechargeFeignFallback {}", throwable.getMessage());
        return new RechargesFeignService(){

            @Override
            public R rechargeCallback(RechargesCallbackRequestDTO requestDTO) {
                log.info("充值回调失败，服务调用降级");
                return R.failed();
            }

            @Override
            public R warehouseFeeDeductions(CustPayDTO dto) {
                log.info("仓储费扣款失败，服务调用降级");
                return R.failed();
            }

            @Override
            public R feeDeductions(CustPayDTO dto) {
                log.info("费扣款失败，服务调用降级");
                return R.failed();
            }

            @Override
            public R freezeBalance(CusFreezeBalanceDTO dto) {
                log.info("冻结余额失败，服务调用降级");
                return R.failed();
            }

            @Override
            public R thawBalance(CusFreezeBalanceDTO dto) {
                log.info("解冻余额失败，服务调用降级");
                return R.failed();
            }

            @Override
            public R<List<AccountBalance>> accountList(AccountBalanceDTO dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R updateUserCredit(UserCreditDTO userCreditDTO) {
                log.info("修改用户授信额度失败，服务调用降级");
                return R.failed();
            }

            @Override
            public R<UserCreditInfoVO> queryUserCredit(String cusCode) {
                log.info("查询用户授信额度失败，服务调用降级");
                return R.failed();
            }

            @Override
            public R onlineIncome(CustPayDTO dto) {
                log.info("充值失败，服务调用降级");
                return R.failed();
            }
        };
    }
}
