package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.api.domain.BasRoute;

import java.util.List;

/**
 * <p>
 * 路由表 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-11-11
 */
public interface IBasRouteService extends IService<BasRoute> {

    /**
     * 查询路由表模块
     *
     * @param id 路由表模块ID
     * @return 路由表模块
     */
    public BasRoute selectBasRouteById(String id);

    /**
     * 查询路由表模块列表
     *
     * @param BasRoute 路由表模块
     * @return 路由表模块集合
     */
    public List<BasRoute> selectBasRouteList(BasRoute basRoute);

    /**
     * 新增路由表模块
     *
     * @param BasRoute 路由表模块
     * @return 结果
     */
    public int insertBasRoute(BasRoute basRoute);

    /**
     * 修改路由表模块
     *
     * @param BasRoute 路由表模块
     * @return 结果
     */
    public int updateBasRoute(BasRoute basRoute);

    /**
     * 批量删除路由表模块
     *
     * @param ids 需要删除的路由表模块ID
     * @return 结果
     */
    public int deleteBasRouteByIds(List<String> ids);

    /**
     * 删除路由表模块信息
     *
     * @param id 路由表模块ID
     * @return 结果
     */
    public int deleteBasRouteById(String id);
}
