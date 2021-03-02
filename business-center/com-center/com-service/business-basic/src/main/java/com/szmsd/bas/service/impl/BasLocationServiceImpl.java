package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasLocationMapper;
import com.szmsd.bas.domain.BasLocation;
import com.szmsd.bas.service.IBasLocationService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-09-18
 */
@Service
public class BasLocationServiceImpl extends ServiceImpl<BasLocationMapper, BasLocation> implements IBasLocationService {


    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BasLocation selectBasLocationById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param BasLocation 模块
     * @return 模块
     */
    @Override
    public List<BasLocation> selectBasLocationList(BasLocation basLocation) {
        QueryWrapper<BasLocation> where = new QueryWrapper<BasLocation>();
        if (StringUtils.isNotEmpty(basLocation.getWarehouseCode())){
            where.eq("warehouse_code",basLocation.getWarehouseCode());
        }
        if (StringUtils.isNotEmpty(basLocation.getWarehouseName())){
            where.like("warehouse_name",basLocation.getWarehouseName());
        }
        if (StringUtils.isNotEmpty(basLocation.getId())){
            where.eq("id",basLocation.getId());
        }
        return baseMapper.selectList(where);
    }

    /**
     * 新增模块
     *
     * @param BasLocation 模块
     * @return 结果
     */
    @Override
    public int insertBasLocation(BasLocation basLocation) {
        return baseMapper.insert(basLocation);
    }

    /**
     * 修改模块
     *
     * @param BasLocation 模块
     * @return 结果
     */
    @Override
    public int updateBasLocation(BasLocation basLocation) {
        return baseMapper.updateById(basLocation);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteBasLocationByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteBasLocationById(String id) {
        return baseMapper.deleteById(id);
    }
}
