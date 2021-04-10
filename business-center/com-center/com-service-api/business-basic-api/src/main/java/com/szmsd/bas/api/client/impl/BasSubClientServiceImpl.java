package com.szmsd.bas.api.client.impl;

import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.api.feign.BasSubFeignService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 18:47
 */
@Service
public class BasSubClientServiceImpl implements BasSubClientService {

    @Autowired
    private BasSubFeignService basSubFeignService;

    @Override
    public Map<String, List<BasSubWrapperVO>> getSub(String code) {
        return R.getDataAndException(this.basSubFeignService.getSub(code));
    }
}
