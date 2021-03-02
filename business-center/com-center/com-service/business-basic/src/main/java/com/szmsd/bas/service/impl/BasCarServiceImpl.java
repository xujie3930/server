package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasCarMapper;
import com.szmsd.bas.domain.BasCar;
import com.szmsd.bas.service.IBasCarService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-09-24
 */
@Service
public class BasCarServiceImpl extends ServiceImpl<BasCarMapper, BasCar> implements IBasCarService {


    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BasCar selectBasCarById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param BasCar 模块
     * @return 模块
     */
    @Override
    public List<BasCar> selectBasCarList(BasCar basCar) {
        QueryWrapper<BasCar> where = new QueryWrapper<BasCar>();
        if (StringUtils.isNotEmpty(basCar.getCarBrand())){
            where.eq("car_brand",basCar.getCarBrand());
        }
        if (StringUtils.isNotEmpty(basCar.getDriver())){
            where.like("driver",basCar.getDriver());
        }
        return baseMapper.selectList(where);
    }

    /**
     * 新增模块
     *
     * @param BasCar 模块
     * @return 结果
     */
    @Override
    public int insertBasCar(BasCar basCar) {
        return baseMapper.insert(basCar);
    }

    /**
     * 修改模块
     *
     * @param BasCar 模块
     * @return 结果
     */
    @Override
    public int updateBasCar(BasCar basCar) {
        return baseMapper.updateById(basCar);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteBasCarByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteBasCarById(String id) {
        return baseMapper.deleteById(id);
    }
}
