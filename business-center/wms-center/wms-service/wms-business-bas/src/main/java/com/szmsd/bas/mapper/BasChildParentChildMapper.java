package com.szmsd.bas.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.bas.domain.BasChildParentChild;
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
public interface BasChildParentChildMapper extends BaseMapper<BasChildParentChild> {

    /**
     * 分页查询列表
     *
     * @param queryVo
     * @return: TableDataInfo
     * @author: taoJie
     * @since: 2022-07-13
     */
    List<BasChildParentChild> pageList(BasChildParentChildQueryVO queryVo);
}