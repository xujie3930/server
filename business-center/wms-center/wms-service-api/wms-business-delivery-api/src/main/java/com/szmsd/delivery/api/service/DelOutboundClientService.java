package com.szmsd.delivery.api.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.domain.DelOutboundPacking;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.vo.*;
import com.szmsd.http.vo.PricedProduct;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 14:35
 */
public interface DelOutboundClientService {

    /**
     * 出库管理 - Open - 接收出库单状态
     *
     * @param dto dto
     * @return int
     */
    int shipment(ShipmentRequestDto dto);

    /**
     * 出库管理 - Open - 接收出库包裹使用包材
     *
     * @param dto dto
     * @return Integer
     */
    int shipmentPacking(ShipmentPackingMaterialRequestDto dto);

    /**
     * 出库管理 - Open - 接收出库包裹使用包材
     *
     * @param dto dto
     * @return int
     */
    int shipmentPackingMaterial(ShipmentPackingMaterialRequestDto dto);

    /**
     * 出库管理 - Open - 接收出库包裹测量信息
     *
     * @param dto dto
     * @return int
     */
    int shipmentPackingMeasure(ShipmentPackingMaterialRequestDto dto);

    /**
     * 出库管理 - Open - 接收批量出库单类型装箱信息
     *
     * @param dto dto
     * @return Integer
     */
    int shipmentContainers(ShipmentContainersRequestDto dto);

    /**
     * 继续处理
     *
     * @param dto dto
     * @return int
     */
    int furtherHandler(DelOutboundFurtherHandlerDto dto);

    /**
     * 取消出库单
     *
     * @param dto dto
     * @return int
     */
    int canceled(DelOutboundCanceledDto dto);

    DelOutboundThirdPartyVO getInfoForThirdParty(DelOutboundVO vo);

    /**
     * 物流服务
     *
     * @param dto dto
     * @return PricedProduct
     */
    List<PricedProduct> inService(DelOutboundOtherInServiceDto dto);

    /**
     * 出库单新增 - DOC支持
     *
     * @param dto dto
     * @return DelOutboundAddResponse
     */
    List<DelOutboundAddResponse> add(List<DelOutboundDto> dto);

    /**
     * 出库单新增
     *
     * @param dto dto
     * @return DelOutboundAddResponse
     */
    DelOutboundAddResponse addShipment(DelOutboundDto dto);

    DelOutboundAddResponse addShipmentPackageCollection(DelOutboundDto dto);

    /**
     * 获取标签
     *
     * @param dto dto
     * @return DelOutboundLabelResponse
     */
    List<DelOutboundLabelResponse> labelBase64(DelOutboundLabelDto dto);

    /**
     * 上传箱标
     *
     * @param dto dto
     * @return int
     */
    int uploadBoxLabel(DelOutboundUploadBoxLabelDto dto);

    /**
     * 装箱信息
     *
     * @param request request
     * @return DelOutboundPacking
     */
    List<DelOutboundPacking> queryList(DelOutboundPacking request);

    /**
     * 装箱信息
     *
     * @param request request
     * @return DelOutboundPackingVO
     */
    List<DelOutboundPackingVO> listByOrderNo(DelOutboundPacking request);

    /**
     * 重新获取挂号
     *
     * @param dto dto
     * @return int
     */
    int againTrackingNo(DelOutboundAgainTrackingNoDto dto);

    /**
     * 获取异常描述
     *
     * @param orderNos orderNos
     * @return DelOutboundListExceptionMessageVO
     */
    List<DelOutboundListExceptionMessageVO> exceptionMessageList(List<String> orderNos);

    /**
     * 获取异常描述
     *
     * @param orderNos orderNos
     * @return DelOutboundListExceptionMessageExportVO
     */
    List<DelOutboundListExceptionMessageExportVO> exceptionMessageExportList(List<String> orderNos);

    /**
     * 提审
     *
     * @param dto
     * @return
     */
    List<DelOutboundBringVerifyVO> bringVerify(@RequestBody @Validated DelOutboundBringVerifyDto dto);

    /**
     * 通知发货
     *
     * @param idList
     */
    void updateShipmentLabel(@RequestBody List<String> idList);

    /**
     * 创建Shopify出库单
     *
     * @param dto dto
     * @return DelOutboundAddResponse
     */
    DelOutboundAddResponse addShopify(DelOutboundDto dto);


    /**
     * 轨迹查询
     *
     * @param queryDto queryDto
     * @return List<DelOutboundReportListVO>
     */
    DelTrackMainCommonDto commonTrackList(List<String> orderNos) ;
}
