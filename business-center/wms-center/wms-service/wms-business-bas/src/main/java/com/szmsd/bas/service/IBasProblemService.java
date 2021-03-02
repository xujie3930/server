package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasProblem;

import java.util.List;

/**
 * <p>
 * 问题件记录表	 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-08-24
 */
public interface IBasProblemService extends IService<BasProblem> {

    /**
     * 查询问题件记录表	模块
     *
     * @param id 问题件记录表	模块ID
     * @return 问题件记录表    模块
     */
    public BasProblem selectBasProblemById(String id);

    /**
     * 查询问题件记录表	模块列表
     *
     * @param BasProblem 问题件记录表	模块
     * @return 问题件记录表    模块集合
     */
    public List<BasProblem> selectBasProblemList(BasProblem basProblem);

    /**
     * 新增问题件记录表	模块
     *
     * @param BasProblem 问题件记录表	模块
     * @return 结果
     */
    public int insertBasProblem(BasProblem basProblem);

    /**
     * 修改问题件记录表	模块
     *
     * @param BasProblem 问题件记录表	模块
     * @return 结果
     */
    public int updateBasProblem(BasProblem basProblem);

    /**
     * 批量删除问题件记录表	模块
     *
     * @param ids 需要删除的问题件记录表	模块ID
     * @return 结果
     */
    public int deleteBasProblemByIds(List
                                             <String> ids);

    /**
     * 删除问题件记录表	模块信息
     *
     * @param id 问题件记录表	模块ID
     * @return 结果
     */
    public int deleteBasProblemById(String id);
}
