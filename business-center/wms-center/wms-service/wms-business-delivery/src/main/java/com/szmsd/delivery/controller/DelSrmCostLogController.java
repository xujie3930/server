package com.szmsd.delivery.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.domain.DelSrmCostLog;
import com.szmsd.delivery.service.IDelSrmCostLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


/**
* <p>
    * 出库单SRM成本调用日志 前端控制器
    * </p>
*
* @author Administrator
* @since 2022-03-04
*/


@Api(tags = {"出库单SRM成本调用日志"})
@RestController
@RequestMapping("/del-srm-cost-log")
public class DelSrmCostLogController extends BaseController{

     @Resource
     private IDelSrmCostLogService delSrmCostLogService;
     /**
       * 查询出库单SRM成本调用日志模块列表
     */
      @PreAuthorize("@ss.hasPermi('DelSrmCostLog:DelSrmCostLog:list')")
      @GetMapping("/list")
      @ApiOperation(value = "查询出库单SRM成本调用日志模块列表",notes = "查询出库单SRM成本调用日志模块列表")
      public TableDataInfo list(DelSrmCostLog delSrmCostLog){
            startPage();
            QueryWrapper<DelSrmCostLog> queryWrapper = Wrappers.query();
            QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "order_no", delSrmCostLog.getOrderNo());
            queryWrapper.orderByAsc("create_time");
            List<DelSrmCostLog> list = delSrmCostLogService.list(queryWrapper);
            return getDataTable(list);
      }


}
