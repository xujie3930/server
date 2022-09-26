package com.szmsd.delivery.service.impl;

import com.szmsd.delivery.domain.DelPrcProductService;
import com.szmsd.delivery.mapper.DelPrcProductServiceMapper;
import com.szmsd.delivery.service.IDelPrcProductServiceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import java.util.List;

/**
* <p>
    * PRC-产品服务 服务实现类
    * </p>
*
* @author admin
* @since 2022-09-26
*/
@Service
public class DelPrcProductServiceServiceImpl extends ServiceImpl<DelPrcProductServiceMapper, DelPrcProductService> implements IDelPrcProductServiceService {


        /**
        * 查询PRC-产品服务模块
        *
        * @param id PRC-产品服务模块ID
        * @return PRC-产品服务模块
        */
        @Override
        public DelPrcProductService selectDelPrcProductServiceById(String id)
        {
        return baseMapper.selectById(id);
        }

        /**
        * 查询PRC-产品服务模块列表
        *
        * @param delPrcProductService PRC-产品服务模块
        * @return PRC-产品服务模块
        */
        @Override
        public List<DelPrcProductService> selectDelPrcProductServiceList(DelPrcProductService delPrcProductService)
        {
        QueryWrapper<DelPrcProductService> where = new QueryWrapper<DelPrcProductService>();
        return baseMapper.selectList(where);
        }

        /**
        * 新增PRC-产品服务模块
        *
        * @param delPrcProductService PRC-产品服务模块
        * @return 结果
        */
        @Override
        public int insertDelPrcProductService(DelPrcProductService delPrcProductService)
        {
        return baseMapper.insert(delPrcProductService);
        }

        /**
        * 修改PRC-产品服务模块
        *
        * @param delPrcProductService PRC-产品服务模块
        * @return 结果
        */
        @Override
        public int updateDelPrcProductService(DelPrcProductService delPrcProductService)
        {
        return baseMapper.updateById(delPrcProductService);
        }

        /**
        * 批量删除PRC-产品服务模块
        *
        * @param ids 需要删除的PRC-产品服务模块ID
        * @return 结果
        */
        @Override
        public int deleteDelPrcProductServiceByIds(List<String>  ids)
       {
            return baseMapper.deleteBatchIds(ids);
       }

        /**
        * 删除PRC-产品服务模块信息
        *
        * @param id PRC-产品服务模块ID
        * @return 结果
        */
        @Override
        public int deleteDelPrcProductServiceById(String id)
        {
        return baseMapper.deleteById(id);
        }



    }

