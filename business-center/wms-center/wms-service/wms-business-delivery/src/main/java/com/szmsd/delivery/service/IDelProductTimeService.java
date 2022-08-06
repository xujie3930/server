package com.szmsd.delivery.service;

import com.szmsd.delivery.domain.DelProductTime;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
* <p>
    *  服务类
    * </p>
*
* @author admin
* @since 2022-08-06
*/
public interface IDelProductTimeService extends IService<DelProductTime> {

        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        DelProductTime selectDelProductTimeById(String id);

        /**
        * 查询模块列表
        *
        * @param delProductTime 模块
        * @return 模块集合
        */
        List<DelProductTime> selectDelProductTimeList(DelProductTime delProductTime);

        /**
        * 新增模块
        *
        * @param delProductTime 模块
        * @return 结果
        */
        int insertDelProductTime(DelProductTime delProductTime);

        /**
        * 修改模块
        *
        * @param delProductTime 模块
        * @return 结果
        */
        int updateDelProductTime(DelProductTime delProductTime);

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        int deleteDelProductTimeByIds(List<String> ids);

        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        int deleteDelProductTimeById(String id);

}

