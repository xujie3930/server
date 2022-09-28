package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.BasFile;

import com.szmsd.delivery.mapper.BasFileMapper;
import com.szmsd.delivery.service.BasFileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BasFileServiceImpl extends ServiceImpl<BasFileMapper, BasFile>  implements BasFileService {
    @Override
    public List<BasFile> selectBasFile(BasFile basFile) {

        LambdaQueryWrapper<BasFile> where = new LambdaQueryWrapper<BasFile>();
        if(StringUtils.isNotEmpty(basFile.getCreateBy())&&!basFile.getCreateBy().equals("admin")){
            where.eq(BasFile::getCreateBy, basFile.getCreateBy());
        }

        if (StringUtils.isNotEmpty(basFile.getStartDate()) && StringUtils.isNotEmpty(basFile.getEndDate())) {
            where.between(BasFile::getCreateTime, basFile.getStartDate(), basFile.getEndDate());
        }


        where.orderByDesc(BasFile::getCreateTime);
        return baseMapper.selectList(where);
    }
}
