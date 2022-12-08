package com.szmsd.finance.api.feign.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.api.feign.AccountSerialBillFeignService;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.dto.AccountOrderQueryDTO;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
public class AccountSerialBillFeignFallback implements FallbackFactory<AccountSerialBillFeignService> {

    @Override
    public AccountSerialBillFeignService create(Throwable throwable) {
        log.info("create失败，服务调用降级{}", throwable);
        return new AccountSerialBillFeignService() {
            @Override
            public R<TableDataInfo<AccountSerialBill>> listPage(AccountSerialBillDTO dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<AccountSerialBill>> selectAccountPrcSerialBill(AccountOrderQueryDTO accountOrderQueryDTO) {
                return R.convertResultJson(throwable);
            }
        };
    }

}
