package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasArea;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-07-04
 */
public interface IBasAreaService extends IService<BasArea> {

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    public BasArea selectBasAreaById(String id);

    /**
     * 查询模块列表
     *
     * @param BasArea 模块
     * @return 模块集合
     */
    public List<BasArea> selectBasAreaList(BasArea basArea);

    /**
     * 新增模块
     *
     * @param BasArea 模块
     * @return 结果
     */
    public int insertBasArea(BasArea basArea);

    /**
     * 修改模块
     *
     * @param BasArea 模块
     * @return 结果
     */
    public int updateBasArea(BasArea basArea);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    public int deleteBasAreaByIds(List
                                          <String> ids);

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    public int deleteBasAreaById(String id);
}
