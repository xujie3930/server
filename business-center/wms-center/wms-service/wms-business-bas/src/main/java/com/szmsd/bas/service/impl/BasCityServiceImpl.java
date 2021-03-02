package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasCityMapper;
import com.szmsd.bas.domain.BasCity;
import com.szmsd.bas.service.IBasCityService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 城市表 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-08-03
 */
@Service
public class BasCityServiceImpl extends ServiceImpl<BasCityMapper, BasCity> implements IBasCityService {


    /**
     * 查询城市表模块
     *
     * @param id 城市表模块ID
     * @return 城市表模块
     */
    @Override
    public BasCity selectBasCityById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询城市表模块列表
     *
     * @param BasCity 城市表模块
     * @return 城市表模块
     */
    @Override
    public List<BasCity> selectBasCityList(BasCity basCity) {
        QueryWrapper<BasCity> where = new QueryWrapper<BasCity>();
        if (StringUtils.isNotEmpty(basCity.getProvinceName())) {
            where.like("province_name", basCity.getProvinceName());
        }
        if (StringUtils.isNotEmpty(basCity.getProvinceCode())) {
            where.eq("province_code", basCity.getProvinceCode());
        }
        if (StringUtils.isNotEmpty(basCity.getCountryCode())) {
            where.eq("country_code", basCity.getCountryCode());
        }
        if (StringUtils.isNotEmpty(basCity.getCountryName())) {
            where.like("country_name", basCity.getCountryName());
        }
        if (StringUtils.isNotEmpty(basCity.getCityName())) {
            where.like("city_name", basCity.getCityName());
        }
        if (StringUtils.isNotEmpty(basCity.getCityCode())) {
            where.eq("city_code", basCity.getCityCode());
        }
        return baseMapper.selectList(where);
    }

    /**
     * 新增城市表模块
     *
     * @param BasCity 城市表模块
     * @return 结果
     */
    @Override
    public int insertBasCity(BasCity basCity) {
        return baseMapper.insert(basCity);
    }

    /**
     * 修改城市表模块
     *
     * @param BasCity 城市表模块
     * @return 结果
     */
    @Override
    public int updateBasCity(BasCity basCity) {
        return baseMapper.updateById(basCity);
    }

    /**
     * 批量删除城市表模块
     *
     * @param ids 需要删除的城市表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasCityByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除城市表模块信息
     *
     * @param id 城市表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasCityById(String id) {
        return baseMapper.deleteById(id);
    }
}
