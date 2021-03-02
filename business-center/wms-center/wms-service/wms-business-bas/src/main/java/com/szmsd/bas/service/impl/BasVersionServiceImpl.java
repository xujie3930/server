package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasVersionMapper;
import com.szmsd.bas.domain.BasVersion;
import com.szmsd.bas.service.IBasVersionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-10-29
 */
@Service
public class BasVersionServiceImpl extends ServiceImpl<BasVersionMapper, BasVersion> implements IBasVersionService {


    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BasVersion selectBasVersionById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param BasVersion 模块
     * @return 模块
     */
    @Override
    public List<BasVersion> selectBasVersionList(BasVersion basVersion) {
        QueryWrapper<BasVersion> where = new QueryWrapper<BasVersion>();

        return baseMapper.selectList(where);
    }

    /**
     * 新增模块
     *
     * @param BasVersion 模块
     * @return 结果
     */
    @Override
    public int insertBasVersion(BasVersion basVersion) {
        return baseMapper.insert(basVersion);
    }

    /**
     * 修改模块
     *
     * @param BasVersion 模块
     * @return 结果
     */
    @Override
    public int updateBasVersion(BasVersion basVersion) {
        return baseMapper.updateById(basVersion);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteBasVersionByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteBasVersionById(String id) {
        return baseMapper.deleteById(id);
    }
}
