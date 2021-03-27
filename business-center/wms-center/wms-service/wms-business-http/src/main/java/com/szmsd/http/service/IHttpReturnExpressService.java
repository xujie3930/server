package com.szmsd.http.service;

import com.szmsd.http.dto.returnex.CreateExpectedReqDTO;
import com.szmsd.http.dto.returnex.ProcessingUpdateReqDTO;
import com.szmsd.http.vo.returnex.CreateExpectedRespVO;
import com.szmsd.http.vo.returnex.ProcessingUpdateRespVO;

/**
 * @ClassName: IHttpReturnExpressService
 * @Description: http
 * @Author: 11
 * @Date: 2021/3/27 9:55
 */
public interface IHttpReturnExpressService {

    /**
     * 创建退件预报
     *  /api/return/expected #F1-VMS 创建退件预报
     * @param expectedReqDTO 创建
     * @return 返回结果
     */
    CreateExpectedRespVO expectedCreate(CreateExpectedReqDTO expectedReqDTO);

    /**
     * 接收客户提供的处理方式
     * /api/return/processing #F2-VMS 接收客户提供的处理方式
     *
     * @param processingUpdateReqDTO 更新数据
     * @return 返回结果
     */
    ProcessingUpdateRespVO processingUpdate(ProcessingUpdateReqDTO processingUpdateReqDTO);
}
