package com.szmsd.exception.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.exception.dto.ExceptionInfoDto;
import com.szmsd.exception.dto.ExceptionInfoQueryDto;
import com.szmsd.exception.dto.NewExceptionRequest;
import com.szmsd.exception.dto.ProcessExceptionRequest;
import com.szmsd.exception.enums.StateSubEnum;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.exception.service.IExceptionInfoService;
import com.szmsd.exception.domain.ExceptionInfo;
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
* @since 2021-03-30
*/


@Api(tags = {"异常信息"})
@RestController
@RequestMapping("/exception/info")
public class ExceptionInfoController extends BaseController{

     @Resource
     private IExceptionInfoService exceptionInfoService;
     /**
       * 查询模块列表
     */
      @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:list')")
      @GetMapping("/list")
      @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
      public TableDataInfo list(ExceptionInfoQueryDto dto)
     {
            startPage();
            List<ExceptionInfo> list = exceptionInfoService.selectExceptionInfoPage(dto);
            return getDataTable(list);
      }

    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:list')")
    @PostMapping("/count")
    @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
    public R<Integer> count(@RequestBody String sellerCode)
    {
        QueryWrapper<ExceptionInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seller_code",sellerCode);
        queryWrapper.eq("state", StateSubEnum.DAICHULI.getCode());
         return R.ok(exceptionInfoService.count(queryWrapper));

    }

    /**
    * 导出模块列表
    */
     @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:export')")
     @Log(title = "模块", businessType = BusinessType.EXPORT)
     @GetMapping("/export")
     @ApiOperation(value = "导出模块列表",notes = "导出模块列表")
     public void export(HttpServletResponse response, ExceptionInfo exceptionInfo) throws IOException {
     List<ExceptionInfo> list = exceptionInfoService.selectExceptionInfoList(exceptionInfo);
     ExcelUtil<ExceptionInfo> util = new ExcelUtil<ExceptionInfo>(ExceptionInfo.class);
        util.exportExcel(response,list, "ExceptionInfo");

     }

    /**
    * 获取模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取模块详细信息",notes = "获取模块详细信息")
    public R getInfo(@PathVariable("id") String id)
    {
    return R.ok(exceptionInfoService.selectExceptionInfoById(id));
    }

    /**
    * 新增模块
    */
    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增模块",notes = "新增模块")
    public R add(@RequestBody NewExceptionRequest newExceptionRequest)
    {
        exceptionInfoService.insertExceptionInfo(newExceptionRequest);
        return R.ok();
    }

    /**
     * 新增模块
     */
    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("process")
    @ApiOperation(value = "处理模块",notes = "处理模块")
    public R process(@RequestBody ProcessExceptionRequest processExceptionRequest)
    {
        exceptionInfoService.processExceptionInfo(processExceptionRequest);
        return R.ok();
    }

    /**
    * 修改模块
    */
    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改模块",notes = "修改模块")
    public R edit(@RequestBody ExceptionInfoDto exceptionInfo)
    {
    return toOk(exceptionInfoService.updateExceptionInfo(exceptionInfo));
    }

    /**
    * 删除模块
    */
    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除模块",notes = "删除模块")
    public R remove(@RequestBody List<String> ids)
    {
    return toOk(exceptionInfoService.deleteExceptionInfoByIds(ids));
    }

}
