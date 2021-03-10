package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.domain.BasePacking;
import com.szmsd.bas.dto.BasePackingQueryDto;
import com.szmsd.bas.mapper.BasePackingMapper;
import com.szmsd.bas.service.IBasePackingService;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author l
* @since 2021-03-06
*/
@Service
public class BasePackingServiceImpl extends ServiceImpl<BasePackingMapper, BasePacking> implements IBasePackingService {


        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        @Override
        public BasePacking selectBasePackingById(String id)
        {
        return baseMapper.selectById(id);
        }

        /**
        * 查询模块列表
        *
        * @param basePacking 模块
        * @return 模块
        */
        @Override
        public List<BasePacking> selectBasePackingList(BasePacking basePacking)
        {
        QueryWrapper<BasePacking> queryWrapper = new QueryWrapper<BasePacking>();
        return baseMapper.selectList(queryWrapper);
        }

        @Override
        public List<BasePacking> selectBasePackingPage(BasePackingQueryDto basePackingQueryDto){
            QueryWrapper<BasePacking> queryWrapper = new QueryWrapper<BasePacking>();
            QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "code", basePackingQueryDto.getCode());
            QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "name", basePackingQueryDto.getName());
            queryWrapper.orderByDesc("create_time");
            return super.list(queryWrapper);
        }

        /**
        * 新增模块
        *
        * @param basePacking 模块
        * @return 结果
        */
        @Override
        public int insertBasePacking(BasePacking basePacking)
        {
        return baseMapper.insert(basePacking);
        }

        /**
        * 修改模块
        *
        * @param basePacking 模块
        * @return 结果
        */
        @Override
        public int updateBasePacking(BasePacking basePacking)
        {
        return baseMapper.updateById(basePacking);
        }

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        @Override
        public int deleteBasePackingByIds(List<String>  ids)
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
        public int deleteBasePackingById(String id)
        {
        return baseMapper.deleteById(id);
        }



    }

