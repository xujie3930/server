package com.szmsd.bas.service;

import com.szmsd.bas.plugin.vo.BasSubWrapperVO;

import java.util.List;

public interface IBasSubCacheService {

    List<BasSubWrapperVO> list(String code);
}
