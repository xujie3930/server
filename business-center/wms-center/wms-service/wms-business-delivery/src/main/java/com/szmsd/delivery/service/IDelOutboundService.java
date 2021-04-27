package com.szmsd.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.vo.DelOutboundDetailListVO;
import com.szmsd.delivery.vo.DelOutboundDetailVO;
import com.szmsd.delivery.vo.DelOutboundListVO;
import com.szmsd.delivery.vo.DelOutboundVO;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;

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
    DelOutboundVO selectDelOutboundById(String id);


    /**
     * 出库-创建采购单
     *
     * @param idList
     * @return
     */
    List<DelOutboundDetailVO> createPurchaseOrderListByIdList(List<String> idList);

    /**
     * 获取转运单详情
     *
     * @param idList
     * @return
     */
    List<DelOutboundDetailVO> getTransshipmentProductData(List<String> idList);

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
     * 批量新增出库单
     *
     * @param dtoList dtoList
     * @return int
     */
    int insertDelOutbounds(List<DelOutboundDto> dtoList);

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

    /**
     * 根据订单id查询出库单
     *
     * @param orderId orderId
     * @return DelOutboundVO
     */
    DelOutbound selectDelOutboundByOrderId(String orderId);

    /**
     * 根据条件查询出库单及详情列表
     *
     * @param queryDto queryDto
     * @return DelOutboundListVO
     */
    List<DelOutboundDetailListVO> getDelOutboundDetailsList(DelOutboundListQueryDto queryDto);

    /**
     * 修改单据状态
     *
     * @param id        id
     * @param stateEnum stateEnum
     */
    void updateState(Long id, DelOutboundStateEnum stateEnum);

    /**
     * 提审失败
     *
     * @param id               id
     * @param exceptionMessage exceptionMessage
     */
    void bringVerifyFail(Long id, String exceptionMessage);

    /**
     * 异常信息
     *
     * @param id               id
     * @param exceptionMessage exceptionMessage
     */
    void exceptionMessage(Long id, String exceptionMessage);

    /**
     * 异常修复
     *
     * @param id id
     */
    void exceptionFix(Long id);

    /**
     * 提审失败
     *
     * @param delOutbound delOutbound
     */
    void bringVerifyFail(DelOutbound delOutbound);

    /**
     * 提审成功
     *
     * @param delOutbound delOutbound
     */
    void bringVerifySuccess(DelOutbound delOutbound);

    /**
     * 出库失败
     *
     * @param delOutbound delOutbound
     */
    void shipmentFail(DelOutbound delOutbound);

    /**
     * 出库成功
     *
     * @param delOutbound delOutbound
     */
    void shipmentSuccess(DelOutbound delOutbound);

    /**
     * 根据单号查询
     *
     * @param orderNo orderNo
     * @return DelOutbound
     */
    DelOutbound getByOrderNo(String orderNo);

    /**
     * 出库单完成
     *
     * @param id id
     */
    void completed(Long id);

    /**
     * 修改完成状态
     *
     * @param id             id
     * @param completedState completedState
     */
    void updateCompletedState(Long id, String completedState);

    /**
     * 修改取消状态
     *
     * @param id             id
     * @param cancelledState cancelledState
     */
    void updateCancelledState(Long id, String cancelledState);

    /**
     * 取消出库单
     *
     * @param dto dto
     * @return int
     */
    int canceled(DelOutboundCanceledDto dto);

    /**
     * 处理出库单
     *
     * @param dto dto
     * @return int
     */
    int handler(DelOutboundHandlerDto dto);

    /**
     * sku导入
     *
     * @param warehouseCode warehouseCode
     * @param sellerCode    sellerCode
     * @param dtoList       dtoList
     * @return List<DelOutboundDetailVO>
     */
    List<DelOutboundDetailVO> importDetail(String warehouseCode, String sellerCode, List<DelOutboundDetailImportDto> dtoList);

    /**
     * 查询出库单费用详情
     *
     * @param queryDto queryDto
     * @return list
     */
    List<QueryChargeVO> getDelOutboundCharge(QueryChargeDto queryDto);

    /**
     * 导出列表查询
     *
     * @param queryDto queryDto
     * @return List<DelOutboundExportListDto>
     */
    List<DelOutboundExportListDto> exportList(DelOutboundListQueryDto queryDto);

}

