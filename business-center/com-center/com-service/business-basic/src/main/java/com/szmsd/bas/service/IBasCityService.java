package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasCity;

import java.util.List;

/**
 * <p>
 * 城市表 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-08-03
 */
public interface IBasCityService extends IService<BasCity> {

    /**
     * 查询城市表模块
     *
     * @param id 城市表模块ID
     * @return 城市表模块
     */
    public BasCity selectBasCityById(String id);

    /**
     * 查询城市表模块列表
     *
     * @param BasCity 城市表模块
     * @return 城市表模块集合
     */
    public List<BasCity> selectBasCityList(BasCity basCity);

    /**
     * 新增城市表模块
     *
     * @param BasCity 城市表模块
     * @return 结果
     */
    public int insertBasCity(BasCity basCity);

    /**
     * 修改城市表模块
     *
     * @param BasCity 城市表模块
     * @return 结果
     */
    public int updateBasCity(BasCity basCity);

    /**
     * 批量删除城市表模块
     *
     * @param ids 需要删除的城市表模块ID
     * @return 结果
     */
    public int deleteBasCityByIds(List
                                          <String> ids);

    /**
     * 删除城市表模块信息
     *
     * @param id 城市表模块ID
     * @return 结果
     */
    public int deleteBasCityById(String id);
}
