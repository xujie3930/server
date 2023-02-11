package com.szmsd.track.api.feign.factory;

import com.szmsd.track.api.feign.TrackFeignService;
import com.szmsd.track.domain.DelTrack;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class TrackFeignFallback implements FallbackFactory<TrackFeignService> {

    @Override
    public TrackFeignService create(Throwable throwable) {

        return new TrackFeignService() {

            @Override
            public void addData(DelTrack delTrack) {

            }
        };

    }
}
