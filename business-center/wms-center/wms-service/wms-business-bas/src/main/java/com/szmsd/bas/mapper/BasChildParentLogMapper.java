package com.szmsd.bas.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.bas.domain.BasChildParentLog;
import com.szmsd.bas.vo.BasChildParentChildQueryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 子母单
 *
 * @author: taoJie
 * @since: 2022-07-13
 */
@Mapper
public interface BasChildParentLogMapper extends BaseMapper<BasChildParentLog> {


    /**
     * 分页查询列表
     *
     * @param queryVo
     * @return: TableDataInfo
     * @author: taoJie
     * @since: 2022-07-13
     */
    List<BasChildParentLog> pageList(BasChildParentChildQueryVO queryVo);
}