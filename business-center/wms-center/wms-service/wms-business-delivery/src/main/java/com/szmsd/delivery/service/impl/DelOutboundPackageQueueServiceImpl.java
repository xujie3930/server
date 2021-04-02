package com.szmsd.delivery.service.impl;

import com.szmsd.delivery.domain.DelOutboundPackageQueue;
import com.szmsd.delivery.mapper.DelOutboundPackageQueueMapper;
import com.szmsd.delivery.service.IDelOutboundPackageQueueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import java.util.List;

/**
* <p>
    * 出库单核重记录 服务实现类
    * </p>
*
* @author asd
* @since 2021-04-02
*/
@Service
public class DelOutboundPackageQueueServiceImpl extends ServiceImpl<DelOutboundPackageQueueMapper, DelOutboundPackageQueue> implements IDelOutboundPackageQueueService {


        /**
        * 查询出库单核重记录模块
        *
        * @param id 出库单核重记录模块ID
        * @return 出库单核重记录模块
        */
        @Override
        public DelOutboundPackageQueue selectDelOutboundPackageQueueById(String id)
        {
        return baseMapper.selectById(id);
        }

        /**
        * 查询出库单核重记录模块列表
        *
        * @param delOutboundPackageQueue 出库单核重记录模块
        * @return 出库单核重记录模块
        */
        @Override
        public List<DelOutboundPackageQueue> selectDelOutboundPackageQueueList(DelOutboundPackageQueue delOutboundPackageQueue)
        {
        QueryWrapper<DelOutboundPackageQueue> where = new QueryWrapper<DelOutboundPackageQueue>();
        return baseMapper.selectList(where);
        }

        /**
        * 新增出库单核重记录模块
        *
        * @param delOutboundPackageQueue 出库单核重记录模块
        * @return 结果
        */
        @Override
        public int insertDelOutboundPackageQueue(DelOutboundPackageQueue delOutboundPackageQueue)
        {
        return baseMapper.insert(delOutboundPackageQueue);
        }

        /**
        * 修改出库单核重记录模块
        *
        * @param delOutboundPackageQueue 出库单核重记录模块
        * @return 结果
        */
        @Override
        public int updateDelOutboundPackageQueue(DelOutboundPackageQueue delOutboundPackageQueue)
        {
        return baseMapper.updateById(delOutboundPackageQueue);
        }

        /**
        * 批量删除出库单核重记录模块
        *
        * @param ids 需要删除的出库单核重记录模块ID
        * @return 结果
        */
        @Override
        public int deleteDelOutboundPackageQueueByIds(List<String>  ids)
       {
            return baseMapper.deleteBatchIds(ids);
       }

        /**
        * 删除出库单核重记录模块信息
        *
        * @param id 出库单核重记录模块ID
        * @return 结果
        */
        @Override
        public int deleteDelOutboundPackageQueueById(String id)
        {
        return baseMapper.deleteById(id);
        }



    }

