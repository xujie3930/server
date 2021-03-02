package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasProvinceMapper;
import com.szmsd.bas.domain.BasProvince;
import com.szmsd.bas.service.IBasProvinceService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 省份表 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-08-03
 */
@Service
public class BasProvinceServiceImpl extends ServiceImpl<BasProvinceMapper, BasProvince> implements IBasProvinceService {


    /**
     * 查询省份表模块
     *
     * @param id 省份表模块ID
     * @return 省份表模块
     */
    @Override
    public BasProvince selectBasProvinceById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询省份表模块列表
     *
     * @param BasProvince 省份表模块
     * @return 省份表模块
     */
    @Override
    public List<BasProvince> selectBasProvinceList(BasProvince basProvince) {
        QueryWrapper<BasProvince> where = new QueryWrapper<BasProvince>();
        if (StringUtils.isNotEmpty(basProvince.getCountryCode())){
            where.eq("country_code",basProvince.getCountryCode());
        }
        if (StringUtils.isNotEmpty(basProvince.getCountryName())){
            where.like("country_name",basProvince.getCountryName());
        }
        if (StringUtils.isNotEmpty(basProvince.getProvinceName())){
            where.like("province_name",basProvince.getProvinceName());
        }
        if (StringUtils.isNotEmpty(basProvince.getProvinceCode())){
            where.eq("province_code",basProvince.getProvinceCode());
        }
        return baseMapper.selectList(where);
    }

    /**
     * 新增省份表模块
     *
     * @param BasProvince 省份表模块
     * @return 结果
     */
    @Override
    public int insertBasProvince(BasProvince basProvince) {
        return baseMapper.insert(basProvince);
    }

    /**
     * 修改省份表模块
     *
     * @param BasProvince 省份表模块
     * @return 结果
     */
    @Override
    public int updateBasProvince(BasProvince basProvince) {
        return baseMapper.updateById(basProvince);
    }

    /**
     * 批量删除省份表模块
     *
     * @param ids 需要删除的省份表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasProvinceByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除省份表模块信息
     *
     * @param id 省份表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasProvinceById(String id) {
        return baseMapper.deleteById(id);
    }
}
