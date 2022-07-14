package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasChildParentLog;
import com.szmsd.bas.service.IBasChildParentLogService;
import com.szmsd.bas.vo.BasChildParentChildQueryVO;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * 子母单
 *
 * @author: taoJie
 * @since: 2022-07-13
 */
@Api(tags = {"子母单"})
@RestController
@RequestMapping("/bas-child-parent-log")
public class BasChildParentLogController extends BaseController {


    @Resource
    private IBasChildParentLogService basChildParentLogService;

    /**
     * 分页查询列表
     *
     * @param queryVo
     * @return: TableDataInfo
     * @author: taoJie
     * @since: 2022-07-13
     */
    @PostMapping("/pageList")
    @ApiOperation(value = "分页查询", notes = "查询列表")
    public TableDataInfo<BasChildParentLog> pageList(@RequestBody BasChildParentChildQueryVO queryVo) {
        startPage(queryVo);
        return getDataTable(basChildParentLogService.pageList(queryVo));
    }


}
