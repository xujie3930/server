package com.szmsd.http.service.impl;

import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.service.IPricedSheetService;
import org.springframework.stereotype.Service;

@Service
public class PricedSheetServiceImpl extends AbstractPricedProductHttpRequest implements IPricedSheetService {

    public PricedSheetServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

}
