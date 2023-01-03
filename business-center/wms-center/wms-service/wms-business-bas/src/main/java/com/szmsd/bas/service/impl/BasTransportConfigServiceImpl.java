package com.szmsd.bas.service.impl;


import com.szmsd.bas.domain.BasTransportConfig;
import com.szmsd.bas.dto.BasTransportConfigDTO;
import com.szmsd.bas.mapper.BasTransportConfigMapper;
import com.szmsd.bas.service.BasTransportConfigService;
import com.szmsd.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BasTransportConfigServiceImpl implements BasTransportConfigService {
    @Autowired
    private BasTransportConfigMapper basTransportConfigMapper;

    @Override
    public List<BasTransportConfig> selectList(BasTransportConfigDTO queryDTO) {
        return basTransportConfigMapper.selectByPrimaryKey(queryDTO);
    }

    @Override
    public R updateBasTransportConfig(BasTransportConfig basTransportConfig) {
        try {
            basTransportConfigMapper.updateByPrimaryKeySelective(basTransportConfig);
         return   R.ok("操作成功");
        }catch (Exception e){
            e.printStackTrace();
            return R.failed();
        }

    }

}
