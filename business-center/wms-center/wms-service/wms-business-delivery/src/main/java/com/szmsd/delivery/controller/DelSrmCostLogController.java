package com.szmsd.delivery.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.delivery.domain.DelSrmCostLog;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.delivery.service.IDelSrmCostLogService;
import com.szmsd.delivery.domain.DelSrmCostLog;
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
    * 出库单SRC成本调用日志 前端控制器
    * </p>
*
* @author Administrator
* @since 2022-03-04
*/


@Api(tags = {"出库单SRC成本调用日志"})
@RestController
@RequestMapping("/del-srm-cost-log")
public class DelSrmCostLogController extends BaseController{

     @Resource
     private IDelSrmCostLogService delSrmCostLogService;
     /**
       * 查询出库单SRC成本调用日志模块列表
     */
      @PreAuthorize("@ss.hasPermi('DelSrmCostLog:DelSrmCostLog:list')")
      @GetMapping("/list")
      @ApiOperation(value = "查询出库单SRC成本调用日志模块列表",notes = "查询出库单SRC成本调用日志模块列表")
      public TableDataInfo list(DelSrmCostLog delSrmCostLog){
            startPage();
            QueryWrapper<DelSrmCostLog> queryWrapper = Wrappers.query();
            QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "order_no", delSrmCostLog.getOrderNo());
            queryWrapper.orderByAsc("create_time");
            List<DelSrmCostLog> list = delSrmCostLogService.list(queryWrapper);
            return getDataTable(list);
      }


}
