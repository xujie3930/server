package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.bas.api.ComBusinessBasicInterface;
import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.bas.service.IBasSubCacheService;
import com.szmsd.bas.service.IBasSubService;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class BasSubCacheServiceImpl implements IBasSubCacheService {

    @Autowired
    private IBasSubService basSubService;

    @Cacheable(value = ComBusinessBasicInterface.NAME + ":Sub", key = "#code", condition = "#code != null")
    @Override
    public List<BasSubWrapperVO> list(String code) {
        QueryWrapper<BasSub> queryWrapper = Wrappers.query();
        queryWrapper.select("main_code", "sub_code", "sub_value", "sub_name", "sub_name_en");
        queryWrapper.eq("main_code", code);
        queryWrapper.orderByAsc("sort");
        List<BasSub> list = this.basSubService.list(queryWrapper);
        if (!CollectionUtils.isEmpty(list)) {
            return BeanMapperUtil.mapList(list, BasSubWrapperVO.class);
        }
        return null;
    }
}
