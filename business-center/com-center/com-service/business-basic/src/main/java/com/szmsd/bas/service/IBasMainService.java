package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.api.domain.BasMain;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-06-18
 */
public interface IBasMainService extends IService<BasMain> {

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    public BasMain selectBasMainById(String id);

    /**
     * 查询模块列表
     *
     * @param BasMain 模块
     * @return 模块集合
     */
    public List<BasMain> selectBasMainList(BasMain basMain);

    /**
     * 新增模块
     *
     * @param BasMain 模块
     * @return 结果
     */
    public int insertBasMain(BasMain basMain);

    /**
     * 修改模块
     *
     * @param BasMain 模块
     * @return 结果
     */
    public int updateBasMain(BasMain basMain);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    public int deleteBasMainByIds(List
                                          <String> ids);

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    public int deleteBasMainById(String id);
}
