package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.DelProductTime;
import com.szmsd.delivery.mapper.DelProductTimeMapper;
import com.szmsd.delivery.service.IDelProductTimeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import java.util.List;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author admin
* @since 2022-08-06
*/
@Service
public class DelProductTimeServiceImpl extends ServiceImpl<DelProductTimeMapper, DelProductTime> implements IDelProductTimeService {


        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        @Override
        public DelProductTime selectDelProductTimeById(String id)
        {
        return baseMapper.selectById(id);
        }

        /**
        * 查询模块列表
        *
        * @param delProductTime 模块
        * @return 模块
        */
        @Override
        public List<DelProductTime> selectDelProductTimeList(DelProductTime delProductTime)
        {
            QueryWrapper<DelProductTime> where = new QueryWrapper<DelProductTime>();
            if(StringUtils.isNotEmpty(delProductTime.getProductCode())){
                where.eq("product_code", delProductTime.getProductCode());
            }
            where.orderByDesc("create_time");
            return baseMapper.selectList(where);
        }

        /**
        * 新增模块
        *
        * @param delProductTime 模块
        * @return 结果
        */
        @Override
        public int insertDelProductTime(DelProductTime delProductTime)
        {

            LambdaQueryWrapper<DelProductTime> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(DelProductTime::getProductCode, delProductTime.getProductCode());
            if(this.getOne(queryWrapper) != null){
                throw new CommonException("999", "重复的产品代码");
            }

            return baseMapper.insert(delProductTime);
        }

        /**
        * 修改模块
        *
        * @param delProductTime 模块
        * @return 结果
        */
        @Override
        public int updateDelProductTime(DelProductTime delProductTime)
        {
            LambdaQueryWrapper<DelProductTime> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.ne(DelProductTime::getId, delProductTime.getId());
            queryWrapper.eq(DelProductTime::getProductCode, delProductTime.getProductCode());
            if(this.getOne(queryWrapper) != null){
                throw new CommonException("999", "重复的产品代码");
            }

            return baseMapper.updateById(delProductTime);
        }

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        @Override
        public int deleteDelProductTimeByIds(List<String>  ids)
       {
            return baseMapper.deleteBatchIds(ids);
       }

        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        @Override
        public int deleteDelProductTimeById(String id)
        {
        return baseMapper.deleteById(id);
        }



    }

