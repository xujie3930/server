package com.szmsd.delivery.service;

import com.szmsd.delivery.domain.DelPrcProductService;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
* <p>
    * PRC-产品服务 服务类
    * </p>
*
* @author admin
* @since 2022-09-26
*/
public interface IDelPrcProductServiceService extends IService<DelPrcProductService> {

        /**
        * 查询PRC-产品服务模块
        *
        * @param id PRC-产品服务模块ID
        * @return PRC-产品服务模块
        */
        DelPrcProductService selectDelPrcProductServiceById(String id);

        /**
        * 查询PRC-产品服务模块列表
        *
        * @param delPrcProductService PRC-产品服务模块
        * @return PRC-产品服务模块集合
        */
        List<DelPrcProductService> selectDelPrcProductServiceList(DelPrcProductService delPrcProductService);

        /**
        * 新增PRC-产品服务模块
        *
        * @param delPrcProductService PRC-产品服务模块
        * @return 结果
        */
        int insertDelPrcProductService(DelPrcProductService delPrcProductService);

        /**
        * 修改PRC-产品服务模块
        *
        * @param delPrcProductService PRC-产品服务模块
        * @return 结果
        */
        int updateDelPrcProductService(DelPrcProductService delPrcProductService);

        /**
        * 批量删除PRC-产品服务模块
        *
        * @param ids 需要删除的PRC-产品服务模块ID
        * @return 结果
        */
        int deleteDelPrcProductServiceByIds(List<String> ids);

        /**
        * 删除PRC-产品服务模块信息
        *
        * @param id PRC-产品服务模块ID
        * @return 结果
        */
        int deleteDelPrcProductServiceById(String id);

}

