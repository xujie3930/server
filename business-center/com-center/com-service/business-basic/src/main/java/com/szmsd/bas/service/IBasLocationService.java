package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasLocation;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-09-18
 */
public interface IBasLocationService extends IService<BasLocation> {

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    public BasLocation selectBasLocationById(String id);

    /**
     * 查询模块列表
     *
     * @param BasLocation 模块
     * @return 模块集合
     */
    public List<BasLocation> selectBasLocationList(BasLocation basLocation);

    /**
     * 新增模块
     *
     * @param BasLocation 模块
     * @return 结果
     */
    public int insertBasLocation(BasLocation basLocation);

    /**
     * 修改模块
     *
     * @param BasLocation 模块
     * @return 结果
     */
    public int updateBasLocation(BasLocation basLocation);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    public int deleteBasLocationByIds(List
                                              <String> ids);

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    public int deleteBasLocationById(String id);
}
