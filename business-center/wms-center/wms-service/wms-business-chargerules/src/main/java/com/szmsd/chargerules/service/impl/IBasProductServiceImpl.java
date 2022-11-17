package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.chargerules.domain.BasProductService;
import com.szmsd.chargerules.mapper.BasProductServiceMapper;
import com.szmsd.chargerules.service.IBasProductService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IBasProductServiceImpl implements IBasProductService {

    @Autowired
    private BasProductServiceMapper basProductServiceMapper;


    @Override
    public List<BasProductService> selectBasProductByCode(List<String> productCodeList) {

        if(CollectionUtils.isEmpty(productCodeList)){
            return new ArrayList<>();
        }

        List<BasProductService> basProductServices = basProductServiceMapper.selectList(Wrappers.<BasProductService>query().lambda().in(BasProductService::getProductCode,productCodeList));

        return basProductServices;
    }
}
