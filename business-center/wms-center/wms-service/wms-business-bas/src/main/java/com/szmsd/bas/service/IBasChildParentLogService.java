package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasChildParentLog;
import com.szmsd.bas.vo.BasChildParentChildQueryVO;

import java.util.List;


/**
 * 子母单
 *
 * @author: taoJie
 * @since: 2022-07-13
 */
public interface IBasChildParentLogService extends IService<BasChildParentLog> {


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
