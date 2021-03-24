package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.api.domain.BasApiCountry;

import java.util.List;

/**
* <p>
    * 第三方接口 - 国家表 服务类
    * </p>
*
* @author admin
* @since 2021-01-20
*/
public interface IBasApiCountryService extends IService<BasApiCountry> {

        /**
        * 查询第三方接口 - 国家表模块
        *
        * @param id 第三方接口 - 国家表模块ID
        * @return 第三方接口 - 国家表模块
        */
        BasApiCountry selectBasApiCountryById(String id);

        /**
        * 查询第三方接口 - 国家表模块列表
        *
        * @param basApiCountry 第三方接口 - 国家表模块
        * @return 第三方接口 - 国家表模块集合
        */
        List<BasApiCountry> selectBasApiCountryList(BasApiCountry basApiCountry);

        /**
        * 新增第三方接口 - 国家表模块
        *
        * @param basApiCountry 第三方接口 - 国家表模块
        * @return 结果
        */
        int insertBasApiCountry(BasApiCountry basApiCountry);

        /**
        * 修改第三方接口 - 国家表模块
        *
        * @param basApiCountry 第三方接口 - 国家表模块
        * @return 结果
        */
        int updateBasApiCountry(BasApiCountry basApiCountry);

        /**
        * 批量删除第三方接口 - 国家表模块
        *
        * @param ids 需要删除的第三方接口 - 国家表模块ID
        * @return 结果
        */
        int deleteBasApiCountryByIds(List<Integer> ids);

        /**
        * 删除第三方接口 - 国家表模块信息
        *
        * @param id 第三方接口 - 国家表模块ID
        * @return 结果
        */
        int deleteBasApiCountryById(Integer id);

        /**
         * 根据名字获取第三方国家信息
         * @param name
         * @return
         */
        BasApiCountry getCountryByName(String name);

}

