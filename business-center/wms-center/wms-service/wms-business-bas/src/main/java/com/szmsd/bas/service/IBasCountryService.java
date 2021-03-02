package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasCountry;

import java.util.List;

/**
 * <p>
 * 国家表 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-08-10
 */
public interface IBasCountryService extends IService<BasCountry> {

    /**
     * 查询国家表模块
     *
     * @param id 国家表模块ID
     * @return 国家表模块
     */
    public BasCountry selectBasCountryById(String id);

    /**
     * 查询国家表模块列表
     *
     * @param BasCountry 国家表模块
     * @return 国家表模块集合
     */
    public List<BasCountry> selectBasCountryList(BasCountry basCountry);

    /**
     * 新增国家表模块
     *
     * @param BasCountry 国家表模块
     * @return 结果
     */
    public int insertBasCountry(BasCountry basCountry);

    /**
     * 修改国家表模块
     *
     * @param BasCountry 国家表模块
     * @return 结果
     */
    public int updateBasCountry(BasCountry basCountry);

    /**
     * 批量删除国家表模块
     *
     * @param ids 需要删除的国家表模块ID
     * @return 结果
     */
    public int deleteBasCountryByIds(List
                                             <String> ids);

    /**
     * 删除国家表模块信息
     *
     * @param id 国家表模块ID
     * @return 结果
     */
    public int deleteBasCountryById(String id);
}
