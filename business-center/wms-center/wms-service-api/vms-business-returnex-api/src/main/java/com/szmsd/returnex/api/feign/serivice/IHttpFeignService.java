package com.szmsd.returnex.api.feign.serivice;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.dto.returnex.CreateExpectedReqDTO;
import com.szmsd.http.dto.returnex.ProcessingUpdateReqDTO;
import com.szmsd.http.vo.returnex.CreateExpectedRespVO;
import com.szmsd.http.vo.returnex.ProcessingUpdateRespVO;
import com.szmsd.returnex.api.feign.serivice.facotry.HttpFeignFallback;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @ClassName: IHttpFeignService
 * @Description: HTTP服务
 * @Author: 11
 * @Date: 2021/3/27 14:05
 */
@FeignClient(value = "wms-business-http", fallbackFactory = HttpFeignFallback.class)
public interface IHttpFeignService {
    /**
     * 创建退件预报
     * /api/return/expected #F1-VMS 创建退件预报
     *
     * @param expectedReqDTO 创建
     * @return 返回结果
     */
    @ApiOperation(value = "创建退件预报", notes = "/api/return/expected #F1-VMS 创建退件预报")
    @PostMapping("/api/return/http/expected")
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
