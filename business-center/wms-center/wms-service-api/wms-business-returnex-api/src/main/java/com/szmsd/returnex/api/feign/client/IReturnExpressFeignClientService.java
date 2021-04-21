package com.szmsd.returnex.api.feign.client;

import com.szmsd.returnex.dto.ReturnArrivalReqDTO;
import com.szmsd.returnex.dto.ReturnProcessingReqDTO;

/**
 * @ClassName: IReturnExpressFeignClientService
 * @Description:
 * @Author: 11
 * @Date: 2021/3/27 14:21
 */
public interface IReturnExpressFeignClientService {

    /**
     * 接收VMS仓库到件信息
     * /api/return/arrival #G1-接收仓库退件到货
     *
     * @param returnArrivalReqDTO 接收VMS仓库到件信息
     * @return 操作结果
     */
    Integer saveArrivalInfoFormWms(ReturnArrivalReqDTO returnArrivalReqDTO);

    /**
     * 接收WMS仓库退件处理结果 结束流程
     * /api/return/processing #G2-接收仓库退件处理
     * 更换 ->  /api/return/done #G3-接收仓库退件处理完成
     *
     * @param returnProcessingReqDTO 接收WMS仓库退件处理结果
     * @return 操作结果
     */
    Integer updateProcessingInfoFromWms(ReturnProcessingReqDTO returnProcessingReqDTO);

    /**
     * 接收仓库拆包明细
     * /api/return/details #G2-接收仓库拆包明细
     *
     * @param returnProcessingReqDTO 拆包明细
     * @return 操作结果
     */
    Integer saveProcessingInfoFromVms(ReturnProcessingReqDTO returnProcessingReqDTO);
}
