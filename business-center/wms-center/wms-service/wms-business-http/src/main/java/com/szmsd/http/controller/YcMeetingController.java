package com.szmsd.http.controller;


import com.szmsd.common.core.domain.R;
import com.szmsd.http.domain.YcAppParameter;
import com.szmsd.http.service.YcMeetingService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/YcMeeting")
public class YcMeetingController {
    @Autowired
    private YcMeetingService ycMeetingService;

    @PostMapping("/http/YcApiri")
    @ApiOperation(value = "调用易仓")
    public  R<Map> YcApiri(@RequestBody YcAppParameter ycAppParameter) {
        return R.ok(ycMeetingService.YcApiri(ycAppParameter));
    }

    @PostMapping("/http/selectBasYcappConfig")
    @ApiOperation(value = "查询所有的客户密钥和key")
    public  R<List<YcAppParameter>> selectBasYcappConfig() {
        return R.ok(ycMeetingService.selectBasYcappConfig());
    }
}
