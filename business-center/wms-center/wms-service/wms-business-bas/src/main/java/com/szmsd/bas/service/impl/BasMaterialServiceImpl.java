package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.szmsd.bas.domain.BasMaterial;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.mapper.BasMaterialMapper;
import com.szmsd.bas.service.IBasMaterialService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private IBasSellerService basSellerService;

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
        QueryWrapper<BasMaterial> queryWrapper = new QueryWrapper<BasMaterial>();
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "code", basMaterial.getCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "seller_code", basMaterial.getSellerCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "type_name", basMaterial.getTypeName());
        queryWrapper.eq("is_active", true);
        queryWrapper.orderByDesc("create_time");
        return baseMapper.selectList(queryWrapper);
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
            //卖家编码
            QueryWrapper<BasSeller> basSellerQueryWrapper = new QueryWrapper<>();
            basSellerQueryWrapper.eq("user_name", SecurityUtils.getLoginUser().getUsername());
            BasSeller basSeller = basSellerService.getOne(basSellerQueryWrapper);
            basMaterial.setSellerCode(basSeller.getSellerCode());
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

