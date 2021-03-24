package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.api.domain.BasSub;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-06-18
 */
public interface IBasSubService extends IService<BasSub> {

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    public BasSub selectBasSubById(String id);

    /**
     * 查询模块列表
     *
     * @param BasSub 模块
     * @return 模块集合
     */
    public List<BasSub> selectBasSubList(BasSub basSub);

    /**
     * 新增模块
     *
     * @param BasSub 模块
     * @return 结果
     */
    public int insertBasSub(BasSub basSub);

    /**
     * 修改模块
     *
     * @param BasSub 模块
     * @return 结果
     */
    public int updateBasSub(BasSub basSub);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    public int deleteBasSubByIds(List
                                         <String> ids);

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    public int deleteBasSubById(String id);
}
