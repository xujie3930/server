package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HttpRechargeFeignServiceFallback;
import com.szmsd.http.dto.recharges.RechargesRequestDTO;
import com.szmsd.http.vo.RechargesResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author liulei
 */
@FeignClient(contextId = "FeignClient.HttpRechargeFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HttpRechargeFeignServiceFallback.class)
public interface HttpRechargeFeignService {

    @PostMapping("/api/recharges/http/onlineRecharge")
    R<RechargesResponseVo> onlineRecharge(@RequestBody RechargesRequestDTO dto);

}
