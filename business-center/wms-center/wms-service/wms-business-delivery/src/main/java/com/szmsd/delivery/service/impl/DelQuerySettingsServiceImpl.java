package com.szmsd.delivery.service.impl;

import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.domain.DelQuerySettings;
import com.szmsd.delivery.mapper.DelQuerySettingsMapper;
import com.szmsd.delivery.service.IDelQuerySettingsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import java.util.List;

/**
* <p>
    * 查件设置 服务实现类
    * </p>
*
* @author Administrator
* @since 2022-06-08
*/
@Service
public class DelQuerySettingsServiceImpl extends ServiceImpl<DelQuerySettingsMapper, DelQuerySettings> implements IDelQuerySettingsService {


        /**
        * 查询查件设置模块
        *
        * @param id 查件设置模块ID
        * @return 查件设置模块
        */
        @Override
        public DelQuerySettings selectDelQuerySettingsById(String id)
        {
        return baseMapper.selectById(id);
        }

        /**
        * 查询查件设置模块列表
        *
        * @param delQuerySettings 查件设置模块
        * @return 查件设置模块
        */
        @Override
        public List<DelQuerySettings> selectDelQuerySettingsList(DelQuerySettings delQuerySettings)
        {
        QueryWrapper<DelQuerySettings> where = new QueryWrapper<DelQuerySettings>();
            where.eq("shipment_rule", delQuerySettings.getShipmentRule());
        return baseMapper.selectList(where);
        }

        /**
        * 新增查件设置模块
        *
        * @param delQuerySettings 查件设置模块
        * @return 结果
        */
        @Override
        public int insertDelQuerySettings(DelQuerySettings delQuerySettings)
        {

            QueryWrapper<DelQuerySettings> where = new QueryWrapper<DelQuerySettings>();
            where.eq("country_code", delQuerySettings.getCountryCode());
            where.eq("shipment_rule", delQuerySettings.getShipmentRule());
            if(baseMapper.selectList(where).size() > 0){
                throw new CommonException("400", "一个国家下不能存在相同的物流服务");
            }

        return baseMapper.insert(delQuerySettings);
        }

        /**
        * 修改查件设置模块
        *
        * @param delQuerySettings 查件设置模块
        * @return 结果
        */
        @Override
        public int updateDelQuerySettings(DelQuerySettings delQuerySettings)
        {
            QueryWrapper<DelQuerySettings> where = new QueryWrapper<DelQuerySettings>();
            where.ne("id", delQuerySettings.getId());
            where.eq("country_code", delQuerySettings.getCountryCode());
            where.eq("shipment_rule", delQuerySettings.getShipmentRule());
            if(baseMapper.selectList(where).size() > 0){
                throw new CommonException("400", "一个国家下不能存在相同的物流服务");
            }

        return baseMapper.updateById(delQuerySettings);
        }

        /**
        * 批量删除查件设置模块
        *
        * @param ids 需要删除的查件设置模块ID
        * @return 结果
        */
        @Override
        public int deleteDelQuerySettingsByIds(List<String>  ids)
       {
            return baseMapper.deleteBatchIds(ids);
       }

        /**
        * 删除查件设置模块信息
        *
        * @param id 查件设置模块ID
        * @return 结果
        */
        @Override
        public int deleteDelQuerySettingsById(String id)
        {
        return baseMapper.deleteById(id);
        }



    }

