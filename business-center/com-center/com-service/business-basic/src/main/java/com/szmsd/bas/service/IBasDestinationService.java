package com.szmsd.bas.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasDestination;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-07-07
 */
public interface IBasDestinationService extends IService<BasDestination> {

    @Resource


    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    public BasDestination selectBasDestinationById(String id);

    /**
     * 查询模块列表
     *
     * @param BasDestination 模块
     * @return 模块集合
     */
    public List<BasDestination> selectBasDestinationList(BasDestination basDestination);

    /**
     * 新增模块
     *
     * @param BasDestination 模块
     * @return 结果
     */
    public int insertBasDestination(BasDestination basDestination);

    /**
     * 修改模块
     *
     * @param BasDestination 模块
     * @return 结果
     */
    public int updateBasDestination(BasDestination basDestination);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    public int deleteBasDestinationByIds(List<String> ids);

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    public int deleteBasDestinationById(String id);


    /**
     * 查询树型
     */
    public JSONArray selectTree(BasDestination basDestination);

    /**
     * 根据网点删除目的地
     * @param businesSiteCode
     * @return
     */
    public int deleteBySiteCode(String businesSiteCode);
}
