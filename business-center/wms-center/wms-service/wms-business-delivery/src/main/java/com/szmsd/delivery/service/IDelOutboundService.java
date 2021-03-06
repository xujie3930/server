package com.szmsd.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundDto;

import java.util.List;

/**
 * <p>
 * 出库单 服务类
 * </p>
 *
 * @author asd
 * @since 2021-03-05
 */
public interface IDelOutboundService extends IService<DelOutbound> {

    /**
     * 查询出库单模块
     *
     * @param id 出库单模块ID
     * @return 出库单模块
     */
    DelOutbound selectDelOutboundById(String id);

    /**
     * 查询出库单模块列表
     *
     * @param delOutbound 出库单模块
     * @return 出库单模块集合
     */
    List<DelOutbound> selectDelOutboundList(DelOutbound delOutbound);

    /**
     * 新增出库单模块
     *
     * @param dto 出库单模块
     * @return 结果
     */
    int insertDelOutbound(DelOutboundDto dto);

    /**
     * 修改出库单模块
     *
     * @param delOutbound 出库单模块
     * @return 结果
     */
    int updateDelOutbound(DelOutbound delOutbound);

    /**
     * 批量删除出库单模块
     *
     * @param ids 需要删除的出库单模块ID
     * @return 结果
     */
    int deleteDelOutboundByIds(List<String> ids);

    /**
     * 删除出库单模块信息
     *
     * @param id 出库单模块ID
     * @return 结果
     */
    int deleteDelOutboundById(String id);

}

