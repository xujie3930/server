package com.szmsd.bas.service.impl;

import com.szmsd.bas.domain.BasMaterial;
import com.szmsd.bas.mapper.BasMaterialMapper;
import com.szmsd.bas.service.IBasMaterialService;
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
* @since 2021-03-12
*/
@Service
public class BasMaterialServiceImpl extends ServiceImpl<BasMaterialMapper, BasMaterial> implements IBasMaterialService {


        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        @Override
        public BasMaterial selectBasMaterialById(String id)
        {
        return baseMapper.selectById(id);
        }

        /**
        * 查询模块列表
        *
        * @param basMaterial 模块
        * @return 模块
        */
        @Override
        public List<BasMaterial> selectBasMaterialList(BasMaterial basMaterial)
        {
        QueryWrapper<BasMaterial> where = new QueryWrapper<BasMaterial>();
        return baseMapper.selectList(where);
        }

        /**
        * 新增模块
        *
        * @param basMaterial 模块
        * @return 结果
        */
        @Override
        public int insertBasMaterial(BasMaterial basMaterial)
        {
        return baseMapper.insert(basMaterial);
        }

        /**
        * 修改模块
        *
        * @param basMaterial 模块
        * @return 结果
        */
        @Override
        public int updateBasMaterial(BasMaterial basMaterial)
        {
        return baseMapper.updateById(basMaterial);
        }

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        @Override
        public int deleteBasMaterialByIds(List<String>  ids)
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
        public int deleteBasMaterialById(String id)
        {
        return baseMapper.deleteById(id);
        }



    }

