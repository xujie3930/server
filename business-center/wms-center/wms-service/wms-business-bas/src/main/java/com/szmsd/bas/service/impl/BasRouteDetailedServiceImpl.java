package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasRouteDetailedMapper;
import com.szmsd.bas.domain.BasRouteDetailed;
import com.szmsd.bas.service.IBasRouteDetailedService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 路由明细表 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-11-11
 */
@Service
public class BasRouteDetailedServiceImpl extends ServiceImpl<BasRouteDetailedMapper, BasRouteDetailed> implements IBasRouteDetailedService {


    /**
     * 查询路由明细表模块
     *
     * @param id 路由明细表模块ID
     * @return 路由明细表模块
     */
    @Override
    public BasRouteDetailed selectBasRouteDetailedById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询路由明细表模块列表
     *
     * @param BasRouteDetailed 路由明细表模块
     * @return 路由明细表模块
     */
    @Override
    public List<BasRouteDetailed> selectBasRouteDetailedList(BasRouteDetailed basRouteDetailed) {
        QueryWrapper<BasRouteDetailed> where = new QueryWrapper<BasRouteDetailed>();
        if (StringUtils.isNotEmpty(basRouteDetailed.getRouteSiteCode())){
            where.eq("route_site_code",basRouteDetailed.getRouteSiteCode());
        }
        if (StringUtils.isNotEmpty(basRouteDetailed.getRouteSiteName())){
            where.eq("route_site_name",basRouteDetailed.getRouteSiteName());
        }
        if (StringUtils.isNotEmpty(basRouteDetailed.getRouteCode())){
            where.eq("route_code",basRouteDetailed.getRouteCode());
        }
        if (StringUtils.isNotEmpty(basRouteDetailed.getRouteName())){
            where.eq("route_name",basRouteDetailed.getRouteName());
        }
        return baseMapper.selectList(where);
    }

    /**
     * 新增路由明细表模块
     *
     * @param BasRouteDetailed 路由明细表模块
     * @return 结果
     */
    @Override
    public int insertBasRouteDetailed(BasRouteDetailed basRouteDetailed) {
        return baseMapper.insert(basRouteDetailed);
    }

    /**
     * 修改路由明细表模块
     *
     * @param BasRouteDetailed 路由明细表模块
     * @return 结果
     */
    @Override
    public int updateBasRouteDetailed(BasRouteDetailed basRouteDetailed) {
        return baseMapper.updateById(basRouteDetailed);
    }

    /**
     * 批量删除路由明细表模块
     *
     * @param ids 需要删除的路由明细表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasRouteDetailedByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除路由明细表模块信息
     *
     * @param id 路由明细表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasRouteDetailedById(String id) {
        return baseMapper.deleteById(id);
    }
}
