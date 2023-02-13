package com.szmsd.track.api.feign.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.track.api.feign.TrackFeignService;
import com.szmsd.track.domain.Track;
import com.szmsd.track.dto.TrackMainDocCommonDto;
import com.szmsd.track.dto.TrackTyRequestLogDto;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrackFeignFallback implements FallbackFactory<TrackFeignService> {

    @Override
    public TrackFeignService create(Throwable throwable) {

        return new TrackFeignService() {

            @Override
            public void addData(Track delTrack) {
                throw new RuntimeException("add Data TrackFeignFallback");
            }

            @Override
            public R<TrackMainDocCommonDto> commonTrackList(List<String> orders) {
                return R.convertResultJson(throwable);
            }

            @Override
            public void pushTY(TrackTyRequestLogDto requestLogDto) {
                throw new RuntimeException("pushTY TrackFeignFallback");
            }

            @Override
            public R<Integer> checkTrackDoc(String orderNo,Integer trackStayDays) {
                return R.convertResultJson(throwable);
            }

            @Override
            public void saveBatch(List<Track> delTrackList) {
                throw new RuntimeException("saveBatch TrackFeignFallback");
            }
        };

    }
}
