package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.DelSrmCostDetail;
import com.szmsd.delivery.mapper.DelSrmCostDetailMapper;
import com.szmsd.delivery.service.IDelSrmCostDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import java.util.List;

/**
* <p>
    * 出库单SRC成本明细 服务实现类
    * </p>
*
* @author Administrator
* @since 2022-03-04
*/
@Service
public class DelSrmCostDetailServiceImpl extends ServiceImpl<DelSrmCostDetailMapper, DelSrmCostDetail> implements IDelSrmCostDetailService {


        /**
        * 查询出库单SRC成本明细模块
        *
        * @param id 出库单SRC成本明细模块ID
        * @return 出库单SRC成本明细模块
        */
        @Override
        public DelSrmCostDetail selectDelSrmCostDetailById(String id)
        {
        return baseMapper.selectById(id);
        }

        /**
        * 查询出库单SRC成本明细模块列表
        *
        * @param delSrmCostDetail 出库单SRC成本明细模块
        * @return 出库单SRC成本明细模块
        */
        @Override
        public List<DelSrmCostDetail> selectDelSrmCostDetailList(DelSrmCostDetail delSrmCostDetail)
        {
        QueryWrapper<DelSrmCostDetail> where = new QueryWrapper<DelSrmCostDetail>();
        return baseMapper.selectList(where);
        }

        /**
        * 新增出库单SRC成本明细模块
        *
        * @param delSrmCostDetail 出库单SRC成本明细模块
        * @return 结果
        */
        @Override
        public int insertDelSrmCostDetail(DelSrmCostDetail delSrmCostDetail)
        {
        return baseMapper.insert(delSrmCostDetail);
        }

        /**
        * 修改出库单SRC成本明细模块
        *
        * @param delSrmCostDetail 出库单SRC成本明细模块
        * @return 结果
        */
        @Override
        public int updateDelSrmCostDetail(DelSrmCostDetail delSrmCostDetail)
        {
        return baseMapper.updateById(delSrmCostDetail);
        }

        /**
        * 批量删除出库单SRC成本明细模块
        *
        * @param ids 需要删除的出库单SRC成本明细模块ID
        * @return 结果
        */
        @Override
        public int deleteDelSrmCostDetailByIds(List<String>  ids)
       {
            return baseMapper.deleteBatchIds(ids);
       }

        /**
        * 删除出库单SRC成本明细模块信息
        *
        * @param id 出库单SRC成本明细模块ID
        * @return 结果
        */
        @Override
        public int deleteDelSrmCostDetailById(String id)
        {
        return baseMapper.deleteById(id);
        }

    @Override
    public DelSrmCostDetail getByOrderNo(String orderNo) {
            if(StringUtils.isEmpty(orderNo)){
                return null;
            }
        LambdaQueryWrapper<DelSrmCostDetail> delSrmCostDetailLambdaQueryWrapper = new LambdaQueryWrapper();
        delSrmCostDetailLambdaQueryWrapper.eq(DelSrmCostDetail::getOrderNo, orderNo);
        DelSrmCostDetail dataDelSrmCostDetail = baseMapper.selectOne(delSrmCostDetailLambdaQueryWrapper);
        return dataDelSrmCostDetail;
    }


}

