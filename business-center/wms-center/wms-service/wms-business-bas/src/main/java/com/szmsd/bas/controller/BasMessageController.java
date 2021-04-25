package com.szmsd.bas.controller;
import com.szmsd.bas.dto.BasMessageQueryDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.bas.service.IBasMessageService;
import com.szmsd.bas.domain.BasMessage;
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
    *  前端控制器
    * </p>
*
* @author l
* @since 2021-04-25
*/


@Api(tags = {"消息通知"})
@RestController
@RequestMapping("/bas/message")
public class BasMessageController extends BaseController{

     @Resource
     private IBasMessageService basMessageService;
     /**
       * 查询模块列表
     */
      @PreAuthorize("@ss.hasPermi('BasMessage:BasMessage:list')")
      @GetMapping("/page")
      @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
      public TableDataInfo list(BasMessageQueryDTO dto)
     {
            startPage();
            List<BasMessage> list = basMessageService.selectBasMessageList(dto);
            return getDataTable(list);
      }

    /**
    * 导出模块列表
    */
     @PreAuthorize("@ss.hasPermi('BasMessage:BasMessage:export')")
     @Log(title = "模块", businessType = BusinessType.EXPORT)
     @GetMapping("/export")
     @ApiOperation(value = "导出模块列表",notes = "导出模块列表")
     public void export(HttpServletResponse response, BasMessageQueryDTO basMessage) throws IOException {
     List<BasMessage> list = basMessageService.selectBasMessageList(basMessage);
     ExcelUtil<BasMessage> util = new ExcelUtil<BasMessage>(BasMessage.class);
        util.exportExcel(response,list, "BasMessage");

     }

    /**
    * 获取模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('BasMessage:BasMessage:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取模块详细信息",notes = "获取模块详细信息")
    public R getInfo(@PathVariable("id") String id)
    {
    return R.ok(basMessageService.selectBasMessageById(id));
    }

    /**
    * 新增模块
    */
    @PreAuthorize("@ss.hasPermi('BasMessage:BasMessage:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增模块",notes = "新增模块")
    public R add(@RequestBody BasMessage basMessage)
    {
        basMessageService.insertBasMessage(basMessage);
        return R.ok();
    }

    /**
    * 修改模块
    */
    @PreAuthorize("@ss.hasPermi('BasMessage:BasMessage:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改模块",notes = "修改模块")
    public R edit(@RequestBody BasMessage basMessage)
    {
    return toOk(basMessageService.updateBasMessage(basMessage));
    }

    /**
    * 删除模块
    */
    @PreAuthorize("@ss.hasPermi('BasMessage:BasMessage:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除模块",notes = "删除模块")
    public R remove(@RequestBody List<Long> ids)
    {
    return toOk(basMessageService.deleteBasMessageByIds(ids));
    }

}
