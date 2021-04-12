package com.szmsd.returnex.api.feign.client.impl;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.domain.R;
import com.szmsd.returnex.api.feign.client.IReturnExpressFeignClientService;
import com.szmsd.returnex.api.feign.serivice.IReturnExpressFeignService;
import com.szmsd.returnex.dto.ReturnArrivalReqDTO;
import com.szmsd.returnex.dto.ReturnProcessingReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName: ReturnExpressFeignClientServiceImpl
 * @Description:
 * @Author: 11
 * @Date: 2021/3/27 14:22
 */
@Slf4j
@Service
public class ReturnExpressFeignClientServiceImpl implements IReturnExpressFeignClientService {

    @Resource
    private IReturnExpressFeignService iReturnExpressFeignService;

    /**
     * 接收VMS仓库到件信息
     * /api/return/arrival #G1-接收仓库退件到货
     *
     * @param returnArrivalReqDTO 接收VMS仓库到件信息
     * @return 操作结果
     */
    @Override
    public int saveArrivalInfoFormVms(ReturnArrivalReqDTO returnArrivalReqDTO) {
        log.info("接受WMS仓库数据 {}", JSONObject.toJSONString(returnArrivalReqDTO));
        return R.getDataAndException(iReturnExpressFeignService.saveArrivalInfoFormVms(returnArrivalReqDTO));
    }

    /**
     * 接收VMS仓库退件处理结果
     * /api/return/processing #G2-接收仓库退件处理
     *
     * @param returnProcessingReqDTO 接收VMS仓库退件处理结果
     * @return 操作结果
     */
    @Override
    public int updateProcessingInfoFromVms(ReturnProcessingReqDTO returnProcessingReqDTO) {
        log.info("接收VMS仓库退件处理结果 {}", JSONObject.toJSONString(returnProcessingReqDTO));
        return R.getDataAndException(iReturnExpressFeignService.updateProcessingInfoFromVms(returnProcessingReqDTO));
    }
}
