package com.szmsd.track.api.feign;

import com.szmsd.delivery.api.BusinessDeliveryInterface;
import com.szmsd.track.api.feign.factory.TrackFeignFallback;
import com.szmsd.track.domain.DelTrack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author wxf
 * @date
 */
@FeignClient(contextId = "FeignClient.TrackFeignService", name = BusinessDeliveryInterface.SERVICE_NAME, fallbackFactory = TrackFeignFallback.class)
public interface TrackFeignService {

    @PostMapping("/api/track/addData")
    void addData(@RequestBody DelTrack delTrack);
}
