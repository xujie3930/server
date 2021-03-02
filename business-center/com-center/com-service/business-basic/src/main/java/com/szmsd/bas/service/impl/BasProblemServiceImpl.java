package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasProblemMapper;
import com.szmsd.bas.domain.BasProblem;
import com.szmsd.bas.service.IBasProblemService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 问题件记录表	 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-08-24
 */
@Service
public class BasProblemServiceImpl extends ServiceImpl<BasProblemMapper, BasProblem> implements IBasProblemService {


    /**
     * 查询问题件记录表	模块
     *
     * @param id 问题件记录表	模块ID
     * @return 问题件记录表    模块
     */
    @Override
    public BasProblem selectBasProblemById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询问题件记录表	模块列表
     *
     * @param BasProblem 问题件记录表	模块
     * @return 问题件记录表    模块
     */
    @Override
    public List<BasProblem> selectBasProblemList(BasProblem basProblem) {
        QueryWrapper<BasProblem> where = new QueryWrapper<BasProblem>();
        if (StringUtils.isNotEmpty(basProblem.getProblemTypeCode())){
            where.eq("problem_type_code",basProblem.getProblemTypeCode());
        }
        return baseMapper.selectList(where);
    }

    /**
     * 新增问题件记录表	模块
     *
     * @param BasProblem 问题件记录表	模块
     * @return 结果
     */
    @Override
    public int insertBasProblem(BasProblem basProblem) {
        return baseMapper.insert(basProblem);
    }

    /**
     * 修改问题件记录表	模块
     *
     * @param BasProblem 问题件记录表	模块
     * @return 结果
     */
    @Override
    public int updateBasProblem(BasProblem basProblem) {
        return baseMapper.updateById(basProblem);
    }

    /**
     * 批量删除问题件记录表	模块
     *
     * @param ids 需要删除的问题件记录表	模块ID
     * @return 结果
     */
    @Override
    public int deleteBasProblemByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除问题件记录表	模块信息
     *
     * @param id 问题件记录表	模块ID
     * @return 结果
     */
    @Override
    public int deleteBasProblemById(String id) {
        return baseMapper.deleteById(id);
    }
}
