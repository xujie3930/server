package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.YcMeetingFeignFallback;
import com.szmsd.http.domain.YcAppParameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "FeignClient.YcMeetingFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = YcMeetingFeignFallback.class)
public interface YcMeetingFeignService {


    @PostMapping("/api/YcMeeting/http/selectBasYcappConfig")
    R<List<YcAppParameter>> selectBasYcappConfig();

    /**
     * 易仓接口调用
     * @param ycAppParameter
     * @return
     */
    @PostMapping("/api/YcMeeting/http/YcApiri")
    R<Map> YcApiri(@RequestBody YcAppParameter ycAppParameter);
}
