package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.BusinessBasInterface;
import com.szmsd.bas.api.factory.BasTransactionFeignFallback;
import com.szmsd.bas.dto.BasTransactionDTO;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "basTransactionFeignService", name = BusinessBasInterface.SERVICE_NAME, fallbackFactory = BasTransactionFeignFallback.class)
public interface BasTransactionFeignService {

    /**
     * 保存接口业务主键
     * @param basTransactionDTO
     * @return
     */
    @PostMapping("/bas/transaction/save")
    R save(BasTransactionDTO basTransactionDTO);

    /**
     * 是否幂等
     * @param basTransactionDTO
     * @return
     */
    @PostMapping("/bas/transaction/idempotent")
    R<Boolean> idempotent(@RequestBody BasTransactionDTO basTransactionDTO);

}
