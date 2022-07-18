package com.szmsd.delivery.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.domain.DelQueryService;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.dto.DelQueryServiceDto;
import com.szmsd.delivery.dto.DelQueryServiceImport;

import java.util.List;

/**
* <p>
    * 查件服务 服务类
    * </p>
*
* @author Administrator
* @since 2022-06-08
*/
public interface IDelQueryServiceService extends IService<DelQueryService> {

        /**
        * 查询查件服务模块
        *
        * @param id 查件服务模块ID
        * @return 查件服务模块
        */
        DelQueryServiceDto selectDelQueryServiceById(String id);

        /**
        * 查询查件服务模块列表
        *
        * @param delQueryService 查件服务模块
        * @return 查件服务模块集合
        */
        List<DelQueryService> selectDelQueryServiceList(DelQueryServiceDto delQueryService);

        /**
        * 新增查件服务模块
        *
        * @param delQueryService 查件服务模块
        * @return 结果
        */
        int insertDelQueryService(DelQueryService delQueryService);

        /**
        * 修改查件服务模块
        *
        * @param delQueryService 查件服务模块
        * @return 结果
        */
        int updateDelQueryService(DelQueryService delQueryService);

        /**
        * 批量删除查件服务模块
        *
        * @param ids 需要删除的查件服务模块ID
        * @return 结果
        */
        int deleteDelQueryServiceByIds(List<String> ids);

        /**
        * 删除查件服务模块信息
        *
        * @param id 查件服务模块ID
        * @return 结果
        */
        int deleteDelQueryServiceById(String id);

        DelQueryServiceDto getOrderInfo(String orderNo);

        R importData(List<DelQueryServiceImport> list);
}

