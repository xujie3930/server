package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.http.dto.returnex.CreateExpectedReqDTO;
import com.szmsd.http.dto.returnex.ProcessingUpdateReqDTO;
import com.szmsd.http.service.IHttpReturnExpressService;
import com.szmsd.http.vo.returnex.CreateExpectedRespVO;
import com.szmsd.http.vo.returnex.ProcessingUpdateRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ClassName: HttpReturnExpressController
 * @Description: 退货服务-请求VMS端接口 HTTP
 * @Author: 11
 * @Date: 2021/3/26 11:42
 */
@Api(tags = {"退货服务-请求VMS端"})
@RestController
@RequestMapping("/api/return/http")
public class HttpReturnExpressController extends BaseController {

    @Resource
    private IHttpReturnExpressService httpReturnExpressService;

    /**
     * 创建退件预报
     * /api/return/expected #F1-VMS 创建退件预报
     *
     * @param expectedReqDTO 创建
     * @return 返回结果
     */
    @PostMapping("/expected")
    @ApiOperation(value = "创建退件预报", notes = "/api/return/expected #F1-VMS 创建退件预报")
    public R<CreateExpectedRespVO> expectedCreate(@RequestBody CreateExpectedReqDTO expectedReqDTO) {
        return R.ok(httpReturnExpressService.expectedCreate(expectedReqDTO));
    }

    /**
     * 接收客户提供的处理方式
     * /api/return/processing #F2-VMS 接收客户提供的处理方式
     *
     * @param processingUpdateReqDTO 更新数据
     * @return 返回结果
     */
    @PutMapping("/processing")
    @ApiOperation(value = "接收客户提供的处理方式", notes = "/api/return/processing #F2-VMS 接收客户提供的处理方式")
    public R<ProcessingUpdateRespVO> processingUpdate(@RequestBody ProcessingUpdateReqDTO processingUpdateReqDTO) {
        return R.ok(httpReturnExpressService.processingUpdate(processingUpdateReqDTO));
    }

}
