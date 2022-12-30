package com.szmsd.http.service.impl;

import com.szmsd.http.config.YcMeetingInterface;
import com.szmsd.http.domain.YcAppParameter;
import com.szmsd.http.mapper.BasYcappConfigMapper;
import com.szmsd.http.service.YcMeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class YcMeetingServiceImpl implements YcMeetingService {

    @Autowired
    private BasYcappConfigMapper basYcappConfigMapper;

    @Override
    public Map YcApiri(YcAppParameter ycAppParameter) {
        YcMeetingInterface ycMeetingInterface=new YcMeetingInterface();
        return ycMeetingInterface.YcApi(ycAppParameter);
    }

    @Override
    public List<YcAppParameter> selectBasYcappConfig() {
        return basYcappConfigMapper.selectByPrimaryKey();
    }
}
