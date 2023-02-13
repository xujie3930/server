package com.szmsd.track.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.track.api.BusinessTrackInterface;
import com.szmsd.track.api.feign.factory.TrackFeignFallback;
import com.szmsd.track.domain.Track;
import com.szmsd.track.dto.TrackMainDocCommonDto;
import com.szmsd.track.dto.TrackTyRequestLogDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author wxf
 * @date
 */
@FeignClient(contextId = "FeignClient.TrackFeignService", name = BusinessTrackInterface.SERVICE_NAME, fallbackFactory = TrackFeignFallback.class)
public interface TrackFeignService {

    @PostMapping("/api/track/addData")
    void addData(@RequestBody Track delTrack);

    @PostMapping("/api/track/commonTrackList")
    R<TrackMainDocCommonDto> commonTrackList(@RequestBody List<String> orders);

    @PostMapping("/api/track/pushTY")
    void pushTY(@RequestBody TrackTyRequestLogDto requestLogDto);

    @GetMapping(value = "/api/track/checkTrackDoc")
    R<Integer> checkTrackDoc(@RequestParam("orderNo")String orderNo,@RequestParam("trackStayDays")Integer trackStayDays);

    @PostMapping("/api/track/saveBatch")
    void saveBatch(@RequestBody List<Track> delTrackList);
}
