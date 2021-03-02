package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasApiCity;

import java.util.List;

/**
* <p>
    * 第三方接口 - 城市表 服务类
    * </p>
*
* @author admin
* @since 2021-01-20
*/
public interface IBasApiCityService extends IService<BasApiCity> {

        /**
        * 查询第三方接口 - 城市表模块
        *
        * @param id 第三方接口 - 城市表模块ID
        * @return 第三方接口 - 城市表模块
        */
        BasApiCity selectBasApiCityById(String id);

        /**
        * 查询第三方接口 - 城市表模块列表
        *
        * @param basApiCity 第三方接口 - 城市表模块
        * @return 第三方接口 - 城市表模块集合
        */
        List<BasApiCity> selectBasApiCityList(BasApiCity basApiCity);

        /**
        * 新增第三方接口 - 城市表模块
        *
        * @param basApiCity 第三方接口 - 城市表模块
        * @return 结果
        */
        int insertBasApiCity(BasApiCity basApiCity);

        /**
        * 修改第三方接口 - 城市表模块
        *
        * @param basApiCity 第三方接口 - 城市表模块
        * @return 结果
        */
        int updateBasApiCity(BasApiCity basApiCity);

        /**
        * 批量删除第三方接口 - 城市表模块
        *
        * @param ids 需要删除的第三方接口 - 城市表模块ID
        * @return 结果
        */
        int deleteBasApiCityByIds(List<Integer> ids);

        /**
        * 删除第三方接口 - 城市表模块信息
        *
        * @param id 第三方接口 - 城市表模块ID
        * @return 结果
        */
        int deleteBasApiCityById(Integer id);

        /**
         * 获取第三方城市信息
         * @param provinceName
         * @param cityName
         * @param townName
         * @return
         */
        BasApiCity getBasApiCity(String provinceName,String cityName,String townName);

}

