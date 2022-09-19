package com.szmsd.bas.service.impl;


import com.szmsd.bas.dao.BasTranslateMapper;
import com.szmsd.bas.domain.BasTranslate;
import com.szmsd.bas.service.ITranslateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ITranslateServiceImpl implements ITranslateService {
   @Autowired
  private BasTranslateMapper basTranslateMapper;

    @Override
    public BasTranslate selectBasTranslate(String query) {
        BasTranslate basTranslate=  basTranslateMapper.selectByPrimaryKey(query);
        return basTranslate;
    }

    @Override
    public int insetBasTranslate(String en, String zh) {
        BasTranslate basTranslat=new BasTranslate();
        basTranslat.setEnName(en);
        basTranslat.setZhName(zh);
        return basTranslateMapper.insertSelective(basTranslat);
    }
}
