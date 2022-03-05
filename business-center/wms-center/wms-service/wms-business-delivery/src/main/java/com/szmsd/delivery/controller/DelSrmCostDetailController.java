package com.szmsd.delivery.controller;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.delivery.domain.DelSrmCostLog;
import com.szmsd.delivery.enums.DelSrmCostLogConstant;
import com.szmsd.delivery.event.DelSrmCostLogEvent;
import com.szmsd.delivery.event.EventUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.delivery.service.IDelSrmCostDetailService;
import com.szmsd.delivery.domain.DelSrmCostDetail;
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
    * 出库单SRC成本明细 前端控制器
    * </p>
*
* @author Administrator
* @since 2022-03-04
*/


@Api(tags = {"出库单SRC成本明细"})
@RestController
@RequestMapping("/del-srm-cost-detail")
public class DelSrmCostDetailController extends BaseController{

     @Resource
     private IDelSrmCostDetailService delSrmCostDetailService;

    @PreAuthorize("@ss.hasPermi('DelSrmCostDetail:DelSrmCostDetail:list')")
    @GetMapping("/list")
    @ApiOperation(value = "出库单SRC成本明细列表", notes = "出库单SRC成本明细列表")
    public TableDataInfo list(DelSrmCostDetail delSrmCostDetail) {
        startPage();
        QueryWrapper<DelSrmCostDetail> queryWrapper = Wrappers.query();
        QueryWrapperUtil.filterDate(queryWrapper, "create_time", delSrmCostDetail.getCreateTimes());
        QueryWrapperUtil.filterDate(queryWrapper, "order_time", delSrmCostDetail.getOrderTimes());

        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.IN, "order_no", delSrmCostDetail.getOrderNo());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.IN, "pd_code", delSrmCostDetail.getPdCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.IN, "productCode", delSrmCostDetail.getProductCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.IN, "cusprice_code", delSrmCostDetail.getCuspriceCode());

        queryWrapper.orderByDesc("create_time");
        List<DelSrmCostDetail> list = delSrmCostDetailService.list(queryWrapper);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('DelSrmCostDetail:DelSrmCostDetail:handler')")
    @PostMapping("/handler")
    @ApiOperation(value = "出库单SRC成本处理", notes = "出库单SRC成本手动处理")
    public R<?> handler(@RequestBody DelSrmCostDetail delSrmCostDetail) {
        Long id = delSrmCostDetail.getId();
        Long[] ids = delSrmCostDetail.getIds();
        if (null == id && ArrayUtils.isEmpty(ids)) {
            throw new CommonException("500", "id或ids不能为空");
        }
        if (ArrayUtils.isNotEmpty(ids)) {
            for (Long aLong : ids) {
                DelSrmCostDetail dataDelSrmCostDetail = delSrmCostDetailService.getById(aLong);
                if(dataDelSrmCostDetail != null){
                    DelSrmCostLog delSrmCostLog = new DelSrmCostLog();
                    delSrmCostLog.setOrderNo(dataDelSrmCostDetail.getOrderNo());
                    delSrmCostLog.setType(DelSrmCostLogConstant.Type.create.name());
                    EventUtil.publishEvent(new DelSrmCostLogEvent(delSrmCostLog));

                }
            }
        }
        return R.ok("已提交异步执行");
    }


}
