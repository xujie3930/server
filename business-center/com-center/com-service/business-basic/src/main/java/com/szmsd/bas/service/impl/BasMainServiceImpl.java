package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasMainMapper;
import com.szmsd.bas.domain.BasMain;
import com.szmsd.bas.service.IBasMainService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-06-18
 */
@Service
public class BasMainServiceImpl extends ServiceImpl<BasMainMapper, BasMain> implements IBasMainService {


    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BasMain selectBasMainById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param BasMain 模块
     * @return 模块
     */
    @Override
    public List<BasMain> selectBasMainList(BasMain basMain) {
        QueryWrapper<BasMain> where = new QueryWrapper<BasMain>();
        if (StringUtils.isNotEmpty(basMain.getMainName())){
            where.like("main_name",basMain.getMainName());
        }
        if (StringUtils.isNotEmpty(basMain.getMainCode())){
            where.like("main_code",basMain.getMainCode());
        }
        return baseMapper.selectList(where);
    }

    /**
     * 新增模块
     *
     * @param BasMain 模块
     * @return 结果
     */
    @Override
    public int insertBasMain(BasMain basMain) {
        return baseMapper.insert(basMain);
    }

    /**
     * 修改模块
     *
     * @param BasMain 模块
     * @return 结果
     */
    @Override
    public int updateBasMain(BasMain basMain) {
        return baseMapper.updateById(basMain);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteBasMainByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteBasMainById(String id) {
        return baseMapper.deleteById(id);
    }
}
