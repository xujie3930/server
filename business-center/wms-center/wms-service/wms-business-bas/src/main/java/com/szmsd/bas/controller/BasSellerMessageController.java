package com.szmsd.bas.controller;
import com.szmsd.bas.domain.BasMessage;
import com.szmsd.bas.dto.BasMessageDto;
import com.szmsd.bas.dto.BasSellerMessageQueryDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.bas.service.IBasSellerMessageService;
import com.szmsd.bas.domain.BasSellerMessage;
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


@Api(tags = {""})
@RestController
@RequestMapping("/bas-seller-message")
public class BasSellerMessageController extends BaseController{

     @Resource
     private IBasSellerMessageService basSellerMessageService;
     /**
       * 查询模块列表
     */
      @PreAuthorize("@ss.hasPermi('BasSellerMessage:BasSellerMessage:list')")
      @GetMapping("/page")
      @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
      public TableDataInfo list(BasSellerMessageQueryDTO dto)
     {
            startPage();
            List<BasMessageDto> list = basSellerMessageService.selectBasSellerMessageList(dto);
            return getDataTable(list);
      }

    /**
    * 导出模块列表
    */
     @PreAuthorize("@ss.hasPermi('BasSellerMessage:BasSellerMessage:export')")
     @Log(title = "模块", businessType = BusinessType.EXPORT)
     @GetMapping("/export")
     @ApiOperation(value = "导出模块列表",notes = "导出模块列表")
     public void export(HttpServletResponse response, BasSellerMessageQueryDTO dto) throws IOException {
     List<BasMessageDto> list = basSellerMessageService.selectBasSellerMessageList(dto);
     ExcelUtil<BasMessageDto> util = new ExcelUtil<BasMessageDto>(BasMessageDto.class);
        util.exportExcel(response,list, "BasSellerMessage");

     }

    /**
    * 获取模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('BasSellerMessage:BasSellerMessage:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取模块详细信息",notes = "获取模块详细信息")
    public R getInfo(@PathVariable("id") String id)
    {
    return R.ok(basSellerMessageService.selectBasSellerMessageById(id));
    }

    @PreAuthorize("@ss.hasPermi('BasSellerMessage:BasSellerMessage:query')")
    @GetMapping(value = "/Bullet")
    @ApiOperation(value = "查询弹框信息",notes = "查询弹框信息")
    public R<List<BasMessageDto>> Bullet(@RequestParam String sellerCode)
    {
        return R.ok(basSellerMessageService.getBulletMessage(sellerCode));
    }

    /**
    * 新增模块
    */
    @PreAuthorize("@ss.hasPermi('BasSellerMessage:BasSellerMessage:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增模块",notes = "新增模块")
    public R add(@RequestBody BasSellerMessage basSellerMessage)
    {
    return toOk(basSellerMessageService.insertBasSellerMessage(basSellerMessage));
    }

    /**
    * 修改模块
    */
    @PreAuthorize("@ss.hasPermi('BasSellerMessage:BasSellerMessage:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改模块",notes = "修改模块")
    public R edit(@RequestBody BasSellerMessage basSellerMessage)
    {
    return toOk(basSellerMessageService.updateBasSellerMessage(basSellerMessage));
    }

    /**
    * 删除模块
    */
    @PreAuthorize("@ss.hasPermi('BasSellerMessage:BasSellerMessage:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除模块",notes = "删除模块")
    public R remove(@RequestBody List<String> ids)
    {
    return toOk(basSellerMessageService.deleteBasSellerMessageByIds(ids));
    }

}
