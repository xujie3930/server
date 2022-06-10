package com.szmsd.delivery.controller;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.delivery.service.IDelQueryServiceFeedbackService;
import com.szmsd.delivery.domain.DelQueryServiceFeedback;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.core.web.page.TableDataInfo;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import java.util.List;
import java.io.IOException;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import com.szmsd.common.core.web.controller.BaseController;


/**
* <p>
    * 查件服务反馈 前端控制器
    * </p>
*
* @author Administrator
* @since 2022-06-08
*/


@Api(tags = {"查件服务反馈"})
@RestController
@RequestMapping("/del-query-service-feedback")
public class DelQueryServiceFeedbackController extends BaseController{

     @Resource
     private IDelQueryServiceFeedbackService delQueryServiceFeedbackService;



    /**
    * 新增查件服务反馈模块
    */
    @PreAuthorize("@ss.hasPermi('DelQueryServiceFeedback:DelQueryServiceFeedback:add')")
    @Log(title = "查件服务反馈模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增查件服务反馈模块",notes = "新增查件服务反馈模块")
    public R add(@RequestBody DelQueryServiceFeedback delQueryServiceFeedback)
    {
    return toOk(delQueryServiceFeedbackService.insertDelQueryServiceFeedback(delQueryServiceFeedback));
    }


}
