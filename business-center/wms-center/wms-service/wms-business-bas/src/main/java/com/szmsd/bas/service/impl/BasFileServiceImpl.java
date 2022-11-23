package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.domain.BasFile;
import com.szmsd.bas.domain.BasFileDao;
import com.szmsd.bas.mapper.BasFileMapper;
import com.szmsd.bas.service.BasFileService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BasFileServiceImpl extends ServiceImpl<BasFileMapper, BasFile>  implements BasFileService {
    @Override
    public List<BasFile> selectBasFile(BasFileDao basFile) {

        LambdaQueryWrapper<BasFile> where = new LambdaQueryWrapper<BasFile>();
        if(StringUtils.isNotEmpty(basFile.getCreateBy())&&!basFile.getCreateBy().equals("admin")){
            where.eq(BasFile::getCreateBy, basFile.getCreateBy());
        }

        if (StringUtils.isNotEmpty(basFile.getStartDate()) && StringUtils.isNotEmpty(basFile.getEndDate())) {
            where.between(BasFile::getCreateTime, basFile.getStartDate(), basFile.getEndDate());
        }

        if (StringUtils.isNotEmpty(basFile.getModularNameZh())) {
            where.eq(BasFile::getModularNameZh, basFile.getModularNameZh());
        }
        if (basFile.getModularNameZhs()!=null&&basFile.getModularNameZhs().size()>0) {
            where.in(BasFile::getModularNameZh, basFile.getModularNameZhs());
        }


        where.orderByDesc(BasFile::getCreateTime);
        return baseMapper.selectList(where);
    }

    @Override
    public R<List<String>> listmodularName() {
        try {
            return R.ok(baseMapper.selectModularName());
        }catch (Exception e){
            e.printStackTrace();
            return  R.failed();
        }

    }
}
