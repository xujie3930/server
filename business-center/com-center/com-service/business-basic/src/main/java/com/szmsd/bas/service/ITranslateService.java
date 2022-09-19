package com.szmsd.bas.service;

import com.szmsd.bas.domain.BasTranslate;

public interface ITranslateService {

    //查询表里面有没有查找的中文

    BasTranslate selectBasTranslate(String query);

    int  insetBasTranslate(String en, String zh);
}
