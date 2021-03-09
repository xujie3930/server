package com.szmsd.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.vo.DelOutboundListVO;

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
     * @param queryDto 出库单模块
     * @return 出库单模块集合
     */
    List<DelOutboundListVO> selectDelOutboundList(DelOutboundListQueryDto queryDto);

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
     * @param dto 出库单模块
     * @return 结果
     */
    int updateDelOutbound(DelOutboundDto dto);

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

    /**
     * 出库管理 - Open - 接收出库单状态
     *
     * @param dto dto
     * @return int
     */
    int shipmentOperationType(ShipmentRequestDto dto);

    /**
     * 出库管理 - Open - 接收出库包裹测量信息
     *
     * @param dto dto
     * @return int
     */
    int shipmentMeasure(PackageMeasureRequestDto dto);

    /**
     * 出库管理 - Open - 接收出库包裹使用包材
     *
     * @param dto dto
     * @return int
     */
    int shipmentPacking(ShipmentPackingMaterialRequestDto dto);

    /**
     * 出库管理 - Open - 接收批量出库单类型装箱信息
     *
     * @param dto dto
     * @return int
     */
    int shipmentContainers(ShipmentContainersRequestDto dto);
}

