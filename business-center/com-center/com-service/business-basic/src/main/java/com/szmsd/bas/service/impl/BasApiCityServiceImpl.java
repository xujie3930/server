package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasApiCity;
import com.szmsd.bas.dao.BasApiCityMapper;
import com.szmsd.bas.service.IBasApiCityService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 第三方接口 - 城市表 服务实现类
 * </p>
 *
 * @author admin
 * @since 2021-01-20
 */
@Service
public class BasApiCityServiceImpl extends ServiceImpl<BasApiCityMapper, BasApiCity> implements IBasApiCityService {


    /**
     * 查询第三方接口 - 城市表模块
     *
     * @param id 第三方接口 - 城市表模块ID
     * @return 第三方接口 - 城市表模块
     */
    @Override
    public BasApiCity selectBasApiCityById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询第三方接口 - 城市表模块列表
     *
     * @param basApiCity 第三方接口 - 城市表模块
     * @return 第三方接口 - 城市表模块
     */
    @Override
    public List<BasApiCity> selectBasApiCityList(BasApiCity basApiCity) {
        QueryWrapper<BasApiCity> where = new QueryWrapper<BasApiCity>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增第三方接口 - 城市表模块
     *
     * @param basApiCity 第三方接口 - 城市表模块
     * @return 结果
     */
    @Override
    public int insertBasApiCity(BasApiCity basApiCity) {
        return baseMapper.insert(basApiCity);
    }

    /**
     * 修改第三方接口 - 城市表模块
     *
     * @param basApiCity 第三方接口 - 城市表模块
     * @return 结果
     */
    @Override
    public int updateBasApiCity(BasApiCity basApiCity) {
        return baseMapper.updateById(basApiCity);
    }

    /**
     * 批量删除第三方接口 - 城市表模块
     *
     * @param ids 需要删除的第三方接口 - 城市表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasApiCityByIds(List<Integer> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除第三方接口 - 城市表模块信息
     *
     * @param id 第三方接口 - 城市表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasApiCityById(Integer id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public BasApiCity getBasApiCity(String provinceName, String cityName, String townName) {
        BasApiCity query =  new BasApiCity();
        //用省份去匹配 name description actual_place_name 三个字段，如果匹配不上 就用城市匹配，如果还匹配不上就用区匹配
        query.setName(provinceName);
        BasApiCity basApiCity = baseMapper.getBasApiCity(query);
        if(basApiCity !=null){
            return basApiCity;
        }
        //城市
        query.setName(cityName);
        basApiCity = baseMapper.getBasApiCity(query);
        if(basApiCity !=null){
            return basApiCity;
        }
        //区
        query.setName(townName);
        basApiCity = baseMapper.getBasApiCity(query);
        if(basApiCity !=null){
            return basApiCity;
        }
        return null;
    }
}

