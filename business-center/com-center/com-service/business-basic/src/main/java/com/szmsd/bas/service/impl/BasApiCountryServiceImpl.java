package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasApiCountryMapper;
import com.szmsd.bas.domain.BasApiCountry;
import com.szmsd.bas.service.IBasApiCountryService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 第三方接口 - 国家表 服务实现类
 * </p>
 *
 * @author admin
 * @since 2021-01-20
 */
@Service
public class BasApiCountryServiceImpl extends ServiceImpl<BasApiCountryMapper, BasApiCountry> implements IBasApiCountryService {


    /**
     * 查询第三方接口 - 国家表模块
     *
     * @param id 第三方接口 - 国家表模块ID
     * @return 第三方接口 - 国家表模块
     */
    @Override
    public BasApiCountry selectBasApiCountryById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询第三方接口 - 国家表模块列表
     *
     * @param basApiCountry 第三方接口 - 国家表模块
     * @return 第三方接口 - 国家表模块
     */
    @Override
    public List<BasApiCountry> selectBasApiCountryList(BasApiCountry basApiCountry) {
        QueryWrapper<BasApiCountry> where = new QueryWrapper<BasApiCountry>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增第三方接口 - 国家表模块
     *
     * @param basApiCountry 第三方接口 - 国家表模块
     * @return 结果
     */
    @Override
    public int insertBasApiCountry(BasApiCountry basApiCountry) {
        return baseMapper.insert(basApiCountry);
    }

    /**
     * 修改第三方接口 - 国家表模块
     *
     * @param basApiCountry 第三方接口 - 国家表模块
     * @return 结果
     */
    @Override
    public int updateBasApiCountry(BasApiCountry basApiCountry) {
        return baseMapper.updateById(basApiCountry);
    }

    /**
     * 批量删除第三方接口 - 国家表模块
     *
     * @param ids 需要删除的第三方接口 - 国家表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasApiCountryByIds(List<Integer> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除第三方接口 - 国家表模块信息
     *
     * @param id 第三方接口 - 国家表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasApiCountryById(Integer id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public BasApiCountry getCountryByName(String name) {
        QueryWrapper<BasApiCountry> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag","0");
        if(StringUtils.isNotEmpty(name)) {
            wrapper.eq("name", name);
        }
        BasApiCountry basApiCountry = baseMapper.selectOne(wrapper);
        if(basApiCountry ==null){
            QueryWrapper<BasApiCountry> where = new QueryWrapper<>();
            where.eq("del_flag","0");
            if(StringUtils.isNotEmpty(name)) {
                where.eq("description", name.toLowerCase());
            }
            basApiCountry = baseMapper.selectOne(wrapper);
        }
        return basApiCountry;
    }


}

