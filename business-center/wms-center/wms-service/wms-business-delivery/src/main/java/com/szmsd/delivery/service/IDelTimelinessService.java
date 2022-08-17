package com.szmsd.delivery.service;

import com.szmsd.delivery.domain.DelTimeliness;

import java.util.List;

public interface IDelTimelinessService {
    List<DelTimeliness> selectDelTimeliness(DelTimeliness delTimeliness);
}
