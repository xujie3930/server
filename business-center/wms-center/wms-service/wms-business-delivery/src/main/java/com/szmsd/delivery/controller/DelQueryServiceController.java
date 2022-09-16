package com.szmsd.delivery.controller;
import com.github.pagehelper.PageInfo;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.dto.DelQueryServiceDto;
import com.szmsd.delivery.dto.DelQueryServiceImport;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.system.api.domain.SysUser;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.delivery.service.IDelQueryServiceService;
import com.szmsd.delivery.domain.DelQueryService;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.core.web.page.TableDataInfo;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.io.IOException;
import java.util.Locale;

import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import com.szmsd.common.core.web.controller.BaseController;
import org.springframework.web.multipart.MultipartFile;


/**
* <p>
    * 查件服务 前端控制器
    * </p>
*
* @author Administrator
* @since 2022-06-08
*/


@Api(tags = {"查件服务"})
@RestController
@RequestMapping("/del-query-service")
public class DelQueryServiceController extends BaseController{

     @Resource
     private IDelQueryServiceService delQueryServiceService;


     /**
       * 查询查件服务模块列表
     */
      @PreAuthorize("@ss.hasPermi('DelQueryService:DelQueryService:list')")
      @GetMapping("/list")
      @ApiOperation(value = "查询查件服务模块列表",notes = "查询查件服务模块列表")
      public R<PageInfo<DelQueryService>> list(DelQueryServiceDto delQueryService)
     {
          return delQueryServiceService.selectDelQueryServiceListrs(delQueryService);
      }


    @PostMapping("/importTemplate")
    @ApiOperation(httpMethod = "POST", value = "导入模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
      String len=getLen().toLowerCase(Locale.ROOT);
      if (len.equals("zh")){
          commonExport(response, "DelQueryService");
      }else if (len.equals("en")){
          commonExport(response, "CombinedParcelOutboundTemplate");
      }

//        ExcelUtil<DelQueryServiceImport> util = new ExcelUtil<DelQueryServiceImport>(DelQueryServiceImport.class);
//        util.importTemplateExcel(response, "DelQueryService");
    }

    private String getFileName(String fileName) {
        return String.format("/template/%s_%s.xlsx", fileName, getLen().toLowerCase(Locale.ROOT));
    }

    private void commonExport(HttpServletResponse response, String fileName) {
        //"/template/退费申请模板.xls"
        ClassPathResource classPathResource = new ClassPathResource(getFileName(fileName));
        try (InputStream inputStream = classPathResource.getInputStream();
             ServletOutputStream outputStream = response.getOutputStream()) {

            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            String excelName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + excelName + ".xls");
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/importData")
    @ApiOperation(httpMethod = "POST", value = "导入查件服务数据")
    public R importData(MultipartFile file, HttpServletRequest httpServletRequest) throws Exception {
        ExcelUtil<DelQueryServiceImport> util = new ExcelUtil<DelQueryServiceImport>(DelQueryServiceImport.class);
        List<DelQueryServiceImport> list = util.importExcel(file.getInputStream());
        list.forEach(x->{
            x.setOperationType(Integer.parseInt(httpServletRequest.getParameter("operationType")));
        });
        return delQueryServiceService.importData(list);
    }

    /**
    * 导出查件服务模块列表
    */
     @PreAuthorize("@ss.hasPermi('DelQueryService:DelQueryService:export')")
     @Log(title = "查件服务模块", businessType = BusinessType.EXPORT)
     @GetMapping("/export")
     @ApiOperation(value = "导出查件服务模块列表",notes = "导出查件服务模块列表")
     public void export(HttpServletResponse response, DelQueryServiceDto delQueryService) throws IOException {
     List<DelQueryService> list = delQueryServiceService.selectDelQueryServiceList(delQueryService);
     ExcelUtil<DelQueryService> util = new ExcelUtil<DelQueryService>(DelQueryService.class);
        util.exportExcel(response,list, "DelQueryService");

     }

    /**
    * 获取查件服务模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('DelQueryService:DelQueryService:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取查件服务模块详细信息",notes = "获取查件服务模块详细信息")
    public R getInfo(@PathVariable("id") String id)
    {
    return R.ok(delQueryServiceService.selectDelQueryServiceById(id));
    }

    /**
     * 根据订单号带出相关信息
     */
    @PreAuthorize("@ss.hasPermi('DelQueryService:DelQueryService:getOrderInfo')")
    @GetMapping(value = "getOrderInfo/{orderNo}/{operationType}")
    @ApiOperation(value = "根据订单号带出相关信息",notes = "根据订单号带出相关信息")
    public R getOrderInfo(@PathVariable("orderNo") String orderNo,@PathVariable("operationType") Integer operationType)
    {
        return R.ok(delQueryServiceService.getOrderInfo(orderNo,operationType));
    }

    /**
    * 新增查件服务模块
    */
    @PreAuthorize("@ss.hasPermi('DelQueryService:DelQueryService:add')")
    @Log(title = "查件服务模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增查件服务模块",notes = "新增查件服务模块")
    public R add(@RequestBody DelQueryService delQueryService)
    {
    return toOk(delQueryServiceService.insertDelQueryService(delQueryService));
    }

    /**
    * 修改查件服务模块
    */
    @PreAuthorize("@ss.hasPermi('DelQueryService:DelQueryService:edit')")
    @Log(title = "查件服务模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改查件服务模块",notes = "修改查件服务模块")
    public R edit(@RequestBody DelQueryService delQueryService)
    {
    return toOk(delQueryServiceService.updateDelQueryService(delQueryService));
    }

    /**
    * 删除查件服务模块
    */
    @PreAuthorize("@ss.hasPermi('DelQueryService:DelQueryService:remove')")
    @Log(title = "查件服务模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除查件服务模块",notes = "删除查件服务模块")
    public R remove(@RequestBody List<String> ids)
    {
    return toOk(delQueryServiceService.deleteDelQueryServiceByIds(ids));
    }

}
