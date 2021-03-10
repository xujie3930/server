package com.szmsd.bas.service.impl;

import com.szmsd.bas.domain.BasSellerInfo;
import com.szmsd.bas.mapper.BasSellerInfoMapper;
import com.szmsd.bas.service.IBasSellerInfoService;
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
* @author l
* @since 2021-03-09
*/
@Service
public class BasSellerInfoServiceImpl extends ServiceImpl<BasSellerInfoMapper, BasSellerInfo> implements IBasSellerInfoService {


        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        @Override
        public BasSellerInfo selectBasSellerInfoById(String id)
        {
        return baseMapper.selectById(id);
        }

        /**
        * 查询模块列表
        *
        * @param basSellerInfo 模块
        * @return 模块
        */
        @Override
        public List<BasSellerInfo> selectBasSellerInfoList(BasSellerInfo basSellerInfo)
        {
        QueryWrapper<BasSellerInfo> where = new QueryWrapper<BasSellerInfo>();
        return baseMapper.selectList(where);
        }

        /**
        * 新增模块
        *
        * @param basSellerInfo 模块
        * @return 结果
        */
        @Override
        public int insertBasSellerInfo(BasSellerInfo basSellerInfo)
        {
        return baseMapper.insert(basSellerInfo);
        }

        /**
        * 修改模块
        *
        * @param basSellerInfo 模块
        * @return 结果
        */
        @Override
        public int updateBasSellerInfo(BasSellerInfo basSellerInfo)
        {
        return baseMapper.updateById(basSellerInfo);
        }

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        @Override
        public int deleteBasSellerInfoByIds(List<String>  ids)
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
        public int deleteBasSellerInfoById(String id)
        {
        return baseMapper.deleteById(id);
        }



    }

