package com.szmsd.delivery.service;

import com.szmsd.delivery.domain.DelQuerySettings;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
* <p>
    * 查件设置 服务类
    * </p>
*
* @author Administrator
* @since 2022-06-08
*/
public interface IDelQuerySettingsService extends IService<DelQuerySettings> {

        /**
        * 查询查件设置模块
        *
        * @param id 查件设置模块ID
        * @return 查件设置模块
        */
        DelQuerySettings selectDelQuerySettingsById(String id);

        /**
        * 查询查件设置模块列表
        *
        * @param delQuerySettings 查件设置模块
        * @return 查件设置模块集合
        */
        List<DelQuerySettings> selectDelQuerySettingsList(DelQuerySettings delQuerySettings);

        /**
        * 新增查件设置模块
        *
        * @param delQuerySettings 查件设置模块
        * @return 结果
        */
        int insertDelQuerySettings(DelQuerySettings delQuerySettings);

        /**
        * 修改查件设置模块
        *
        * @param delQuerySettings 查件设置模块
        * @return 结果
        */
        int updateDelQuerySettings(DelQuerySettings delQuerySettings);

        /**
        * 批量删除查件设置模块
        *
        * @param ids 需要删除的查件设置模块ID
        * @return 结果
        */
        int deleteDelQuerySettingsByIds(List<String> ids);

        /**
        * 删除查件设置模块信息
        *
        * @param id 查件设置模块ID
        * @return 结果
        */
        int deleteDelQuerySettingsById(String id);

}

