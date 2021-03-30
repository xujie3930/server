package com.szmsd.returnex.api.feign.client;

import com.szmsd.http.dto.returnex.CreateExpectedReqDTO;
import com.szmsd.http.dto.returnex.ProcessingUpdateReqDTO;
import com.szmsd.http.vo.returnex.CreateExpectedRespVO;
import com.szmsd.http.vo.returnex.ProcessingUpdateRespVO;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @ClassName: IReturnExpressFeignClientService
 * @Description: 通过HTTP服务发起 http请求调用外部VMS接口
 * @Author: 11
 * @Date: 2021/3/27 14:21
 */
public interface IHttpFeignClientService {

    /**
     * 创建退件预报
     * /api/return/expected #F1-VMS 创建退件预报
     *
     * @param expectedReqDTO 创建
     * @return 返回结果
     */
    CreateExpectedRespVO expectedCreate(@RequestBody CreateExpectedReqDTO expectedReqDTO);

    /**
     * 接收客户提供的处理方式
     * /api/return/processing #F2-VMS 接收客户提供的处理方式
     *
     * @param processingUpdateReqDTO 更新数据
     * @return 返回结果
     */
    ProcessingUpdateRespVO processingUpdate(@RequestBody ProcessingUpdateReqDTO processingUpdateReqDTO);
}
