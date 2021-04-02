package com.szmsd.delivery.service;

import com.szmsd.delivery.domain.DelOutboundPackageQueue;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
* <p>
    * 出库单核重记录 服务类
    * </p>
*
* @author asd
* @since 2021-04-02
*/
public interface IDelOutboundPackageQueueService extends IService<DelOutboundPackageQueue> {

        /**
        * 查询出库单核重记录模块
        *
        * @param id 出库单核重记录模块ID
        * @return 出库单核重记录模块
        */
        DelOutboundPackageQueue selectDelOutboundPackageQueueById(String id);

        /**
        * 查询出库单核重记录模块列表
        *
        * @param delOutboundPackageQueue 出库单核重记录模块
        * @return 出库单核重记录模块集合
        */
        List<DelOutboundPackageQueue> selectDelOutboundPackageQueueList(DelOutboundPackageQueue delOutboundPackageQueue);

        /**
        * 新增出库单核重记录模块
        *
        * @param delOutboundPackageQueue 出库单核重记录模块
        * @return 结果
        */
        int insertDelOutboundPackageQueue(DelOutboundPackageQueue delOutboundPackageQueue);

        /**
        * 修改出库单核重记录模块
        *
        * @param delOutboundPackageQueue 出库单核重记录模块
        * @return 结果
        */
        int updateDelOutboundPackageQueue(DelOutboundPackageQueue delOutboundPackageQueue);

        /**
        * 批量删除出库单核重记录模块
        *
        * @param ids 需要删除的出库单核重记录模块ID
        * @return 结果
        */
        int deleteDelOutboundPackageQueueByIds(List<String> ids);

        /**
        * 删除出库单核重记录模块信息
        *
        * @param id 出库单核重记录模块ID
        * @return 结果
        */
        int deleteDelOutboundPackageQueueById(String id);

}

