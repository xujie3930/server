package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.szmsd.bas.domain.BasOtherRules;
import com.szmsd.bas.mapper.BasOtherRulesMapper;
import com.szmsd.bas.service.IBasOtherRulesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import java.util.List;

/**
* <p>
    * 其他规则匹配 服务实现类
    * </p>
*
* @author Administrator
* @since 2022-05-16
*/
@Service
public class BasOtherRulesServiceImpl extends ServiceImpl<BasOtherRulesMapper, BasOtherRules> implements IBasOtherRulesService {


        /**
        * 查询其他规则匹配模块
        *
        * @param sellerCode 客户编号
        * @return 其他规则匹配模块
        */
        @Override
        public BasOtherRules selectBasOtherRulesById(String sellerCode)
        {


            LambdaQueryWrapper<BasOtherRules> where = new LambdaQueryWrapper<BasOtherRules>();
            where.eq(BasOtherRules::getSellerCode, sellerCode);
            BasOtherRules dataBasOtherRules = baseMapper.selectOne(where);

            return dataBasOtherRules;
        }

        /**
        * 查询其他规则匹配模块列表
        *
        * @param basOtherRules 其他规则匹配模块
        * @return 其他规则匹配模块
        */
        @Override
        public List<BasOtherRules> selectBasOtherRulesList(BasOtherRules basOtherRules)
        {
        QueryWrapper<BasOtherRules> where = new QueryWrapper<BasOtherRules>();
        return baseMapper.selectList(where);
        }

        /**
        * 新增其他规则匹配模块
        *
        * @param basOtherRules 其他规则匹配模块
        * @return 结果
        */
        @Override
        public int addOrUpdate(BasOtherRules basOtherRules)
        {

            BasOtherRules dataBasOtherRules = selectBasOtherRulesById(basOtherRules.getSellerCode());
            if(dataBasOtherRules != null){
                basOtherRules.setId(dataBasOtherRules.getId());
                return baseMapper.updateById(basOtherRules);
            }else{
                return baseMapper.insert(basOtherRules);


            }


        }

        /**
        * 修改其他规则匹配模块
        *
        * @param basOtherRules 其他规则匹配模块
        * @return 结果
        */
        @Override
        public int updateBasOtherRules(BasOtherRules basOtherRules)
        {
        return baseMapper.updateById(basOtherRules);
        }

        /**
        * 批量删除其他规则匹配模块
        *
        * @param ids 需要删除的其他规则匹配模块ID
        * @return 结果
        */
        @Override
        public int deleteBasOtherRulesByIds(List<String>  ids)
       {
            return baseMapper.deleteBatchIds(ids);
       }

        /**
        * 删除其他规则匹配模块信息
        *
        * @param id 其他规则匹配模块ID
        * @return 结果
        */
        @Override
        public int deleteBasOtherRulesById(String id)
        {
        return baseMapper.deleteById(id);
        }



    }

