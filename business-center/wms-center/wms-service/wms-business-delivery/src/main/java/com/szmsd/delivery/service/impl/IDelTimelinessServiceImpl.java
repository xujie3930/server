package com.szmsd.delivery.service.impl;

import com.szmsd.delivery.domain.DelTimeliness;
import com.szmsd.delivery.mapper.DelTimelinessMapper;
import com.szmsd.delivery.service.IDelTimelinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IDelTimelinessServiceImpl implements IDelTimelinessService {
    @Autowired
    private DelTimelinessMapper delTimelinessMapper;
    @Override
    public List<DelTimeliness> selectDelTimeliness(DelTimeliness delTimeliness) {
        List<DelTimeliness> list=  delTimelinessMapper.selectDelTimeliness(delTimeliness);
        return list;
    }
}
