package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.returnex.CreateExpectedReqDTO;
import com.szmsd.http.dto.returnex.ProcessingUpdateReqDTO;
import com.szmsd.http.service.IHttpReturnExpressService;
import com.szmsd.http.vo.returnex.CreateExpectedRespVO;
import com.szmsd.http.vo.returnex.ProcessingUpdateRespVO;
import org.springframework.stereotype.Service;

/**
 * @ClassName: HttpReturnExpressReqImpl
 * @Description: 远程调用VMS仓库接口
 * @Author: 11
 * @Date: 2021/3/27 9:57
 */
@Service
public class HttpReturnExpressReqImpl extends AbstractReturnExpressReq implements IHttpReturnExpressService {

    public HttpReturnExpressReqImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    /**
     * 创建退件预报
     *  /api/return/expected #F1-VMS 创建退件预报
     * @param expectedReqDTO 创建
     * @return 返回结果
     */
    @Override
    public CreateExpectedRespVO expectedCreate(CreateExpectedReqDTO expectedReqDTO) {
        return JSON.parseObject(httpPost(httpConfig.getReturnExpressConfig().getExpectedCreate(), expectedReqDTO), CreateExpectedRespVO.class);
    }

    /**
     * 接收客户提供的处理方式
     * /api/return/processing #F2-VMS 接收客户提供的处理方式
     *
     * @param processingUpdateReqDTO 更新数据
     * @return 返回结果
     */
    @Override
    public ProcessingUpdateRespVO processingUpdate(ProcessingUpdateReqDTO processingUpdateReqDTO) {
        return JSON.parseObject(httpPut(httpConfig.getReturnExpressConfig().getProcessingUpdate(), processingUpdateReqDTO), ProcessingUpdateRespVO.class);
    }
}
