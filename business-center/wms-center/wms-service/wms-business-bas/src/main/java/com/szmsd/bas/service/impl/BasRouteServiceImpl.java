package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasRouteMapper;
import com.szmsd.bas.domain.BasRoute;
import com.szmsd.bas.service.IBasRouteService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 路由表 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-11-11
 */
@Service
public class BasRouteServiceImpl extends ServiceImpl<BasRouteMapper, BasRoute> implements IBasRouteService {


    /**
     * 查询路由表模块
     *
     * @param id 路由表模块ID
     * @return 路由表模块
     */
    @Override
    public BasRoute selectBasRouteById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询路由表模块列表
     *
     * @param BasRoute 路由表模块
     * @return 路由表模块
     */
    @Override
    public List<BasRoute> selectBasRouteList(BasRoute basRoute) {
        QueryWrapper<BasRoute> where = new QueryWrapper<BasRoute>();
        if (StringUtils.isNotEmpty(basRoute.getRouteName())) {
            where.like("route_name", basRoute.getRouteName());
        }
        if (StringUtils.isNotEmpty(basRoute.getStartStation())) {
            where.like("start_station", basRoute.getStartStation());
        }
        if (StringUtils.isNotEmpty(basRoute.getRouteCode())) {
            where.eq("route_code", basRoute.getRouteCode());
        }
        if (StringUtils.isNotEmpty(basRoute.getStartStationCode())) {
            where.eq("start_station_code", basRoute.getStartStationCode());
        }
        if (StringUtils.isNotEmpty(basRoute.getEndStationCode())) {
            where.eq("end_station_code", basRoute.getEndStationCode());
        }
        return baseMapper.selectList(where);
    }

    /**
     * 新增路由表模块
     *
     * @param BasRoute 路由表模块
     * @return 结果
     */
    @Override
    public int insertBasRoute(BasRoute basRoute) {
        return baseMapper.insert(basRoute);
    }

    /**
     * 修改路由表模块
     *
     * @param BasRoute 路由表模块
     * @return 结果
     */
    @Override
    public int updateBasRoute(BasRoute basRoute) {
        return baseMapper.updateById(basRoute);
    }

    /**
     * 批量删除路由表模块
     *
     * @param ids 需要删除的路由表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasRouteByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除路由表模块信息
     *
     * @param id 路由表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasRouteById(String id) {
        return baseMapper.deleteById(id);
    }
}
