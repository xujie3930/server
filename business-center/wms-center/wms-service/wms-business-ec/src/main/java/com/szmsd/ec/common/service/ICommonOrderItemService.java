package com.szmsd.ec.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.ec.domain.CommonOrderItem;

import java.util.List;

/**
 * <p>
 * 电商平台公共订单明细表 服务类
 * </p>
 *
 * @author zengfanlang
 * @since 2021-12-17
 */
public interface ICommonOrderItemService extends IService<CommonOrderItem> {

    /**
     * 查询电商平台公共订单明细表模块
     *
     * @param id 电商平台公共订单明细表模块ID
     * @return 电商平台公共订单明细表模块
     */
    CommonOrderItem selectEcCommonOrderItemById(String id);

    /**
     * 查询电商平台公共订单明细表模块列表
     *
     * @param commonOrderItem 电商平台公共订单明细表模块
     * @return 电商平台公共订单明细表模块集合
     */
    List<CommonOrderItem> selectEcCommonOrderItemList(CommonOrderItem commonOrderItem);

    /**
     * 新增电商平台公共订单明细表模块
     *
     * @param commonOrderItem 电商平台公共订单明细表模块
     * @return 结果
     */
    int insertEcCommonOrderItem(CommonOrderItem commonOrderItem);

    /**
     * 修改电商平台公共订单明细表模块
     *
     * @param commonOrderItem 电商平台公共订单明细表模块
     * @return 结果
     */
    int updateEcCommonOrderItem(CommonOrderItem commonOrderItem);

    /**
     * 批量删除电商平台公共订单明细表模块
     *
     * @param ids 需要删除的电商平台公共订单明细表模块ID
     * @return 结果
     */
    int deleteEcCommonOrderItemByIds(List<String> ids);

    /**
     * 删除电商平台公共订单明细表模块信息
     *
     * @param id 电商平台公共订单明细表模块ID
     * @return 结果
     */
    int deleteEcCommonOrderItemById(String id);

}

