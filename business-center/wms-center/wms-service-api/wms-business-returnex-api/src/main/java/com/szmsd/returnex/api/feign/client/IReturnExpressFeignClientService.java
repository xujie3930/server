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
    int saveArrivalInfoFormVms(ReturnArrivalReqDTO returnArrivalReqDTO);

    /**
     * 接收VMS仓库退件处理结果
     * /api/return/processing #G2-接收仓库退件处理
     *
     * @param returnProcessingReqDTO 接收VMS仓库退件处理结果
     * @return 操作结果
     */
    int updateProcessingInfoFromVms(ReturnProcessingReqDTO returnProcessingReqDTO);
}
