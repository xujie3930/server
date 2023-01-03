package com.szmsd.bas.service.impl;


import com.szmsd.bas.mapper.BasTransportConfigMapper;
import com.szmsd.bas.service.BasTransportConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BasTransportConfigServiceImpl implements BasTransportConfigService {
    @Autowired
    private BasTransportConfigMapper basTransportConfigMapper;

}
