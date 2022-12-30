package com.szmsd.http.service;

import com.szmsd.http.domain.YcAppParameter;

import java.util.List;
import java.util.Map;

public interface YcMeetingService {
    Map YcApiri(YcAppParameter ycAppParameter);

    List<YcAppParameter>  selectBasYcappConfig();
}
