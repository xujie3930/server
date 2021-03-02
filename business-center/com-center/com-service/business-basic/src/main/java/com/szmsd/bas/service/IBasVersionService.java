package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasVersion;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-10-29
 */
public interface IBasVersionService extends IService<BasVersion> {

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    public BasVersion selectBasVersionById(String id);

    /**
     * 查询模块列表
     *
     * @param BasVersion 模块
     * @return 模块集合
     */
    public List<BasVersion> selectBasVersionList(BasVersion basVersion);

    /**
     * 新增模块
     *
     * @param BasVersion 模块
     * @return 结果
     */
    public int insertBasVersion(BasVersion basVersion);

    /**
     * 修改模块
     *
     * @param BasVersion 模块
     * @return 结果
     */
    public int updateBasVersion(BasVersion basVersion);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    public int deleteBasVersionByIds(List<String> ids);

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    public int deleteBasVersionById(String id);
}
