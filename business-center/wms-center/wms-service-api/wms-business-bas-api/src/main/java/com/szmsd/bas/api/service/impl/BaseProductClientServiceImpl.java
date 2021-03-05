package com.szmsd.bas.api.service.impl;

import com.szmsd.bas.api.feign.BaseProductFeignService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseProductClientServiceImpl implements BaseProductClientService {
    @Autowired
    private BaseProductFeignService baseProductFeignService;

    @Override
    public Boolean checkSkuvalid(String code){
        BaseProduct baseProduct = new BaseProduct();
        baseProduct.setCode(code);
        return this.baseProductFeignService.checkSkuvalid(baseProduct).getData();
    }
}
