package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.domain.BasChildParentLog;
import com.szmsd.bas.mapper.BasChildParentLogMapper;
import com.szmsd.bas.service.IBasChildParentLogService;
import com.szmsd.bas.vo.BasChildParentChildQueryVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 子母单
 *
 * @author: taoJie
 * @since: 2022-07-13
 */
@Service
public class BasChildParentLogServiceImpl extends ServiceImpl<BasChildParentLogMapper, BasChildParentLog> implements IBasChildParentLogService {

    @Override
    public List<BasChildParentLog> pageList(BasChildParentChildQueryVO queryVo) {
        return baseMapper.pageList(queryVo);
    }
}

