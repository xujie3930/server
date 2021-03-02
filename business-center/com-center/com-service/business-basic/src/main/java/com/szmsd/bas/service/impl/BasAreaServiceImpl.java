package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasAreaMapper;
import com.szmsd.bas.domain.BasArea;
import com.szmsd.bas.service.IBasAreaService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-07-04
 */
@Service
public class BasAreaServiceImpl extends ServiceImpl<BasAreaMapper, BasArea> implements IBasAreaService {


    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BasArea selectBasAreaById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param BasArea 模块
     * @return 模块
     */
    @Override
    public List<BasArea> selectBasAreaList(BasArea basArea) {
        QueryWrapper<BasArea> where = new QueryWrapper<BasArea>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增模块
     *
     * @param BasArea 模块
     * @return 结果
     */
    @Override
    public int insertBasArea(BasArea basArea) {
        return baseMapper.insert(basArea);
    }

    /**
     * 修改模块
     *
     * @param BasArea 模块
     * @return 结果
     */
    @Override
    public int updateBasArea(BasArea basArea) {
        return baseMapper.updateById(basArea);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteBasAreaByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteBasAreaById(String id) {
        return baseMapper.deleteById(id);
    }
}
