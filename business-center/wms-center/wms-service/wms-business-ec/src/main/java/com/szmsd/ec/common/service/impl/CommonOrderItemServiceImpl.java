package com.szmsd.ec.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.utils.StringToolkit;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.ec.common.mapper.CommonOrderItemMapper;
import com.szmsd.ec.common.service.ICommonOrderItemService;
import com.szmsd.ec.domain.CommonOrderItem;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 电商平台公共订单明细表 服务实现类
 * </p>
 *
 * @author zengfanlang
 * @since 2021-12-17
 */
@Service
public class CommonOrderItemServiceImpl extends ServiceImpl<CommonOrderItemMapper, CommonOrderItem> implements ICommonOrderItemService {


    /**
     * 查询电商平台公共订单明细表模块
     *
     * @param id 电商平台公共订单明细表模块ID
     * @return 电商平台公共订单明细表模块
     */
    @Override
    public CommonOrderItem selectEcCommonOrderItemById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询电商平台公共订单明细表模块列表
     *
     * @param commonOrderItem 电商平台公共订单明细表模块
     * @return 电商平台公共订单明细表模块
     */
    @Override
    public List<CommonOrderItem> selectEcCommonOrderItemList(CommonOrderItem commonOrderItem) {
        LambdaQueryWrapper<CommonOrderItem> where = new LambdaQueryWrapper<CommonOrderItem>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增电商平台公共订单明细表模块
     *
     * @param commonOrderItem 电商平台公共订单明细表模块
     * @return 结果
     */
    @Override
    public int insertEcCommonOrderItem(CommonOrderItem commonOrderItem) {
        return baseMapper.insert(commonOrderItem);
    }

    /**
     * 修改电商平台公共订单明细表模块
     *
     * @param commonOrderItem 电商平台公共订单明细表模块
     * @return 结果
     */
    @Override
    public int updateEcCommonOrderItem(CommonOrderItem commonOrderItem) {
        return baseMapper.updateById(commonOrderItem);
    }

    /**
     * 批量删除电商平台公共订单明细表模块
     *
     * @param ids 需要删除的电商平台公共订单明细表模块ID
     * @return 结果
     */
    @Override
    public int deleteEcCommonOrderItemByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除电商平台公共订单明细表模块信息
     *
     * @param id 电商平台公共订单明细表模块ID
     * @return 结果
     */
    @Override
    public int deleteEcCommonOrderItemById(String id) {
        return baseMapper.deleteById(id);
    }


}

