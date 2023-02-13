package com.szmsd.track.controller;

import com.szmsd.track.domain.DelTrack;
import com.szmsd.track.service.IDelTrackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/api/track")
public class DelTrackApiController {

    @Resource
    private IDelTrackService delTrackService;

    @PostMapping("/addData")
    public void addData(@RequestBody DelTrack delTrack) {

        delTrackService.addData(delTrack);
    }
}
