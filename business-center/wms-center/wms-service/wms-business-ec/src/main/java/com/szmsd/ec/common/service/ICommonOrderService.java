package com.szmsd.ec.common.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.ec.domain.CommonOrder;
import com.szmsd.ec.dto.CommonOrderDTO;
import com.szmsd.ec.dto.LabelCountDTO;
import com.szmsd.ec.enums.OrderSourceEnum;

import java.util.List;

/**
 * <p>
 * 电商平台公共订单表 服务类
 * </p>
 *
 * @author zengfanlang
 * @since 2021-12-17
 */
public interface ICommonOrderService extends IService<CommonOrder> {


    /**
     * 同步第三方电商平台订单至中间表
     * @param orderSourceEnum
     * @param t
     */
    <T> void syncCommonOrder(OrderSourceEnum orderSourceEnum, T t);

    List<LabelCountDTO> getCountByStatus(Wrapper<CommonOrder> queryWrapper);

    void transferWarehouseOrder(CommonOrderDTO commonOrderDTO);

    /**
     * 发货
     * @param ids
     */
    void orderShipping(List<Long> ids);
}

