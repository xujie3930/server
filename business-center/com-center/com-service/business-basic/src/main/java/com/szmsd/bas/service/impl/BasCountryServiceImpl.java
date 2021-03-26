package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasCountry;
import com.szmsd.bas.dao.BasCountryMapper;
import com.szmsd.bas.service.IBasCountryService;
import com.szmsd.common.core.utils.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 国家表 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-08-10
 */
@Service
public class BasCountryServiceImpl extends ServiceImpl<BasCountryMapper, BasCountry> implements IBasCountryService {


    /**
     * 查询国家表模块
     *
     * @param id 国家表模块ID
     * @return 国家表模块
     */
    @Override
    public BasCountry selectBasCountryById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询国家表模块列表
     *
     * @param BasCountry 国家表模块
     * @return 国家表模块
     */
    @Override
    public List<BasCountry> selectBasCountryList(BasCountry basCountry) {
        QueryWrapper<BasCountry> where = new QueryWrapper<BasCountry>();
        if (StringUtils.isNotEmpty(basCountry.getCountryCode())) {
            where.like("country_code", basCountry.getCountryCode());
        }
        if (StringUtils.isNotEmpty(basCountry.getCountryName())) {
            where.like("country_name", basCountry.getCountryName());
        }

        return baseMapper.selectList(where);
    }

    /**
     * 新增国家表模块
     *
     * @param BasCountry 国家表模块
     * @return 结果
     */
    @Override
    public int insertBasCountry(BasCountry basCountry) {
        return baseMapper.insert(basCountry);
    }

    /**
     * 修改国家表模块
     *
     * @param BasCountry 国家表模块
     * @return 结果
     */
    @Override
    public int updateBasCountry(BasCountry basCountry) {
        return baseMapper.updateById(basCountry);
    }

    /**
     * 批量删除国家表模块
     *
     * @param ids 需要删除的国家表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasCountryByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除国家表模块信息
     *
     * @param id 国家表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasCountryById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public BasCountry queryByCountryCode(String countryCode) {
        LambdaQueryWrapper<BasCountry> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(BasCountry::getCountryCode, countryCode);
        List<BasCountry> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }
}
