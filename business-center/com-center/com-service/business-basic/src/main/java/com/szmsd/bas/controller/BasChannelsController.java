package com.szmsd.bas.controller;

import com.szmsd.bas.api.domain.BasChannels;
import com.szmsd.bas.api.domain.dto.BasChannelsDTO;
import com.szmsd.bas.service.IBasChannelsService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 渠道维护 前端控制器
 * </p>
 *
 * @author jun
 * @since 2022-07-09
 */
@Api(tags = {"渠道维护"})
@RestController
@RequestMapping("/basChannelsController")
public class BasChannelsController extends BaseController {

    @Autowired
    private IBasChannelsService iBasChannelsService;


    /**
     * 添加修改渠道节点维护信息
     * @param basChannels
     * @return
     */
    @PostMapping(value = "/insertBasChannels")
    @ApiOperation(value = "新增和修改渠道节点",notes = "新增和修改渠道节点")
    @Log(title = "新增或修改改渠道节点", businessType = BusinessType.INSERT)
    public R insertBasChannels(@RequestBody BasChannels basChannels){
        R r=iBasChannelsService.insertBasChannels(basChannels);
        return  r;
    }

    /**
     * 查询接口
     * @param basChannelsDTO
     * @return
     */
    @PostMapping(value = "/selectBasChannels")
    @ApiOperation(value = "查询渠道节点",notes = "查询渠道节点")
    public TableDataInfo selectBasChannels(@RequestBody BasChannelsDTO basChannelsDTO){
        startPage(basChannelsDTO);
        R<List<BasChannels>> r = iBasChannelsService.selectBasChannels(basChannelsDTO);
        return getDataTable(r.getData());
    }

    /**
     * 删除接口
     * @param basChannelsDTO
     * @return
     */
    @PostMapping(value = "/deleteBasChannels")
    @ApiOperation(value = "删除接口",notes = "删除接口(传入主键id就可)")
    public R deleteBasChannels(@RequestBody BasChannelsDTO basChannelsDTO){

        R r = iBasChannelsService.deleteBasChannels(basChannelsDTO);
        return r;
    }
}
