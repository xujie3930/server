package com.szmsd.track.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.track.domain.Track;
import com.szmsd.track.dto.TrackMainCommonDto;
import com.szmsd.track.dto.TrackTyRequestLogDto;
import com.szmsd.track.service.IDelTrackService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 轨迹模块api
 */
@Slf4j
@RestController
@RequestMapping("/api/track")
public class DelTrackApiController {

    @Resource
    private IDelTrackService delTrackService;

    @PostMapping("/addData")
    public void addData(@RequestBody Track delTrack) {

        delTrackService.addData(delTrack);
    }

    @PostMapping("/commonTrackList")
    @ApiOperation(value = "查询模块列表", notes = "查询模块列表")
    public R<TrackMainCommonDto> commonTrackList(@RequestBody List<String> orderNos) {

        return delTrackService.commonTrackList(orderNos);
    }

    @PostMapping("/pushTY")
    public void pushTY(@RequestBody TrackTyRequestLogDto requestLogDto) {

        delTrackService.pushTY(requestLogDto);
    }

    @GetMapping("/checkTrackDoc")
    public R<Integer> checkTrackDoc(@RequestParam("orderNo")String orderNo,@RequestParam("trackStayDays")Integer trackStayDays){
        return delTrackService.checkTrackDoc(orderNo,trackStayDays);
    }

    @PostMapping("/pushTY")
    public void saveBatch(@RequestBody List<Track> delTrackList) {
        delTrackService.saveBatch(delTrackList);
    }
}
