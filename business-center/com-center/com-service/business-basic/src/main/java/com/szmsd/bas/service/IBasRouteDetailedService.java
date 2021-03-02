package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasRouteDetailed;

import java.util.List;

/**
 * <p>
 * 路由明细表 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-11-11
 */
public interface IBasRouteDetailedService extends IService<BasRouteDetailed> {

    /**
     * 查询路由明细表模块
     *
     * @param id 路由明细表模块ID
     * @return 路由明细表模块
     */
    public BasRouteDetailed selectBasRouteDetailedById(String id);

    /**
     * 查询路由明细表模块列表
     *
     * @param BasRouteDetailed 路由明细表模块
     * @return 路由明细表模块集合
     */
    public List<BasRouteDetailed> selectBasRouteDetailedList(BasRouteDetailed basRouteDetailed);

    /**
     * 新增路由明细表模块
     *
     * @param BasRouteDetailed 路由明细表模块
     * @return 结果
     */
    public int insertBasRouteDetailed(BasRouteDetailed basRouteDetailed);

    /**
     * 修改路由明细表模块
     *
     * @param BasRouteDetailed 路由明细表模块
     * @return 结果
     */
    public int updateBasRouteDetailed(BasRouteDetailed basRouteDetailed);

    /**
     * 批量删除路由明细表模块
     *
     * @param ids 需要删除的路由明细表模块ID
     * @return 结果
     */
    public int deleteBasRouteDetailedByIds(List<String> ids);

    /**
     * 删除路由明细表模块信息
     *
     * @param id 路由明细表模块ID
     * @return 结果
     */
    public int deleteBasRouteDetailedById(String id);
}
