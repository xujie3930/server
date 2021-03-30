package com.szmsd.returnex.api.feign.serivice;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.dto.returnex.CreateExpectedReqDTO;
import com.szmsd.http.dto.returnex.ProcessingUpdateReqDTO;
import com.szmsd.http.vo.returnex.CreateExpectedRespVO;
import com.szmsd.http.vo.returnex.ProcessingUpdateRespVO;
import com.szmsd.returnex.api.feign.serivice.facotry.HttpFeignServiceFallback;
import com.szmsd.returnex.api.feign.serivice.facotry.ReturnExpressFeignFallback;
import com.szmsd.returnex.dto.ReturnArrivalReqDTO;
import com.szmsd.returnex.dto.ReturnProcessingReqDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @ClassName: IReturnExpressFeignClientService
 * @Description: 通过HTTP服务发起 http请求调用外部VMS接口
 * @Author: 11
 * @Date: 2021/3/27 14:21
 */
@FeignClient(contextId = "feignClient.IHttpFeignService",value = "wms-business-http", fallbackFactory = HttpFeignServiceFallback.class)
public interface IHttpFeignService {

    /**
     * 创建退件预报
     * /api/return/expected #F1-VMS 创建退件预报
     *
     * @param expectedReqDTO 创建
     * @return 返回结果
     */
    @PostMapping("/api/return/http/expected")
    @ApiOperation(value = "创建退件预报", notes = "/api/return/expected #F1-VMS 创建退件预报")
    R<CreateExpectedRespVO> expectedCreate(@RequestBody CreateExpectedReqDTO expectedReqDTO);

    /**
     * 接收客户提供的处理方式
     * /api/return/processing #F2-VMS 接收客户提供的处理方式
     *
     * @param processingUpdateReqDTO 更新数据
     * @return 返回结果
     */
    @PutMapping("/api/return/http/processing")
    @ApiOperation(value = "接收客户提供的处理方式", notes = "/api/return/processing #F2-VMS 接收客户提供的处理方式")
    R<ProcessingUpdateRespVO> processingUpdate(@RequestBody ProcessingUpdateReqDTO processingUpdateReqDTO);
}
