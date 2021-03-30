package com.szmsd.finance.service.impl;

import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.bas.api.feign.BasSubFeignService;
import com.szmsd.finance.service.ISysDictDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liulei
 */
@Service
public class SysDictDataServiceImpl implements ISysDictDataService {
    
    @Autowired
    BasSubFeignService basSubFeignService;
    
    @Override
    public String getCurrencyNameByCode(String currencyCode) {
        List<BasSub> basSubs = basSubFeignService.listApi("008",currencyCode);
        if(CollectionUtils.isEmpty(basSubs)){
            return "";
        }
        return basSubs.get(0).getSubName();
    }
}
