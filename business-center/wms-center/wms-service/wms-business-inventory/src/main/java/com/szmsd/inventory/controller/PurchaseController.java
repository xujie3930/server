package com.szmsd.inventory.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.QueryPage;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.inventory.domain.dto.PurchaseAddDTO;
import com.szmsd.inventory.domain.dto.PurchaseQueryDTO;
import com.szmsd.inventory.domain.dto.TransportWarehousingAddDTO;
import com.szmsd.inventory.domain.excel.PurchaseInfoDetailExcle;
import com.szmsd.inventory.domain.vo.PurchaseInfoListVO;
import com.szmsd.inventory.domain.vo.PurchaseInfoVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.inventory.service.IPurchaseService;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.core.web.page.TableDataInfo;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import com.szmsd.common.core.web.controller.BaseController;
import org.springframework.web.multipart.MultipartFile;


/**
 * <p>
 * 采购单 前端控制器
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */


@Api(tags = {"采购单"})
@RestController
@RequestMapping("/purchase")
public class PurchaseController extends BaseController {

    @Resource
    private IPurchaseService purchaseService;

    /**
     * 查询采购单模块列表
     */
    @PreAuthorize("@ss.hasPermi('Purchase:Purchase:list')")
    @GetMapping("/list")
    @ApiOperation(value = "【服务端】查询采购单模块列表", notes = "查询采购单模块列表")
    public TableDataInfo<PurchaseInfoListVO> list(PurchaseQueryDTO purchaseQueryDTO) {
        startPage();
        List<PurchaseInfoListVO> list = purchaseService.selectPurchaseList(purchaseQueryDTO);
        return getDataTable(list);
    }

    /**
     * 查询采购单模块列表
     */
    @PreAuthorize("@ss.hasPermi('Purchase:Purchase:list')")
    @GetMapping("/client/list")
    @ApiOperation(value = "【客户端】查询采购单模块列表", notes = "查询采购单模块列表")
    public TableDataInfo<PurchaseInfoListVO> listClient(PurchaseQueryDTO purchaseQueryDTO) {
        startPage();
        List<PurchaseInfoListVO> list = purchaseService.selectPurchaseListClient(purchaseQueryDTO);
        return getDataTable(list);
    }

    /**
     * 获取采购单模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('Purchase:Purchase:query')")
    @GetMapping(value = "getInfo/{purchaseNo}")
    @ApiOperation(value = "详情-通过采购单号", notes = "获取采购单模块详细信息")
    public R<PurchaseInfoVO> getInfo(@PathVariable("purchaseNo") String purchaseNo) {
        return R.ok(purchaseService.selectPurchaseByPurchaseNo(purchaseNo));
    }

    /**
     * 导出采购单(导出一对多合并单元格)
     */
    @GetMapping("/exportus")
    @ApiOperation(value = "导出模块列表", notes = "导出模块列表")
    public void exportus(HttpServletResponse response, Integer id) throws IOException {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CommonException("500", "非法的操作");
        }
        List<PurchaseInfoDetailExcle> list=purchaseService.selectPurchaseInfoDetailEx(id);



        ExportParams params = new ExportParams();
//        params.setTitle("异常通知中心_异常导出");




        Workbook workbook = ExcelExportUtil.exportExcel(params, PurchaseInfoDetailExcle.class, list);


        Sheet sheet= workbook.getSheet("sheet0");

        //获取第一行数据
        Row row2 =sheet.getRow(0);

        for (int i=0;i<5;i++){
            Cell deliveryTimeCell = row2.getCell(i);

            CellStyle styleMain = workbook.createCellStyle();
            if (i==4){
                styleMain.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            }else {
                styleMain.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());

            }
            Font font = workbook.createFont();
            //true为加粗，默认为不加粗
            font.setBold(true);
            //设置字体颜色，颜色和上述的颜色对照表是一样的
            font.setColor(IndexedColors.WHITE.getIndex());
            //将字体样式设置到单元格样式中
            styleMain.setFont(font);

            styleMain.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleMain.setAlignment(HorizontalAlignment.CENTER);
            styleMain.setVerticalAlignment(VerticalAlignment.CENTER);
//        CellStyle style =  workbook.createCellStyle();
//        style.setFillPattern(HSSFColor.HSSFColorPredefined.valueOf(""));
//        style.setFillForegroundColor(IndexedColors.RED.getIndex());
            deliveryTimeCell.setCellStyle(styleMain);
        }

        //获取第二行数据
        Row row3 =sheet.getRow(1);
        for (int x=4;x<6;x++) {
            Cell deliveryTimeCell1 = row3.getCell(x);
            CellStyle styleMain1 = workbook.createCellStyle();
            styleMain1.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            Font font1 = workbook.createFont();
            //true为加粗，默认为不加粗
            font1.setBold(true);
            //设置字体颜色，颜色和上述的颜色对照表是一样的
            font1.setColor(IndexedColors.WHITE.getIndex());
            //将字体样式设置到单元格样式中
            styleMain1.setFont(font1);

            styleMain1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleMain1.setAlignment(HorizontalAlignment.CENTER);
            styleMain1.setVerticalAlignment(VerticalAlignment.CENTER);
            deliveryTimeCell1.setCellStyle(styleMain1);
        }

        try {
            String fileName="采购单明细数据"+System.currentTimeMillis();
            URLEncoder.encode(fileName, "UTF-8");
            //response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xls");

            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");

            ServletOutputStream outStream = null;
            try {
                outStream = response.getOutputStream();
                workbook.write(outStream);
                outStream.flush();
            } finally {
                outStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //@PreAuthorize("@ss.hasPermi('')")
    @PostMapping("/importPurchaseInfoDetailExcle")
    @ApiOperation(value = "导入采购单明细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R<Map<String, Object>> importPurchaseInfoDetailExcle(@RequestPart("file") MultipartFile file,HttpServletRequest request) {

        AssertUtil.notNull(file, "上传文件不存在");
        try {

            //采购主表id
            String associationId=request.getParameter("associationId");
            String purchaseNo=request.getParameter("purchaseNo");

            //导入新的解析方法
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(2);
            List<PurchaseInfoDetailExcle> list = ExcelImportUtil.importExcel(file.getInputStream(),PurchaseInfoDetailExcle.class,params);
           Map map= purchaseService.importPurchaseInfoDetailExcle(list,associationId,purchaseNo);
            return R.ok(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.failed(e.getMessage());
        }
    }



    /**
     * 导出采购单异常数据(导出一对多合并单元格)
     */
    @GetMapping("/exportusAbnormal")
    @ApiOperation(value = "导出模块列表", notes = "导出模块列表")
    public void exportusAbnormal(HttpServletResponse response, Integer id) throws IOException {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CommonException("500", "非法的操作");
        }

        //导出的异常数据
        List<PurchaseInfoDetailExcle> list=purchaseService.exportusAbnormal(id);



        //拿到值之后做删除异常数据操作
        purchaseService.deletePurchaseStorageDetails(id);


        ExportParams params = new ExportParams();
//        params.setTitle("异常通知中心_异常导出");


       if (list.size()>0){



        Workbook workbook = ExcelExportUtil.exportExcel(params, PurchaseInfoDetailExcle.class, list);


        Sheet sheet= workbook.getSheet("sheet0");

        //获取第一行数据
        Row row2 =sheet.getRow(0);

        for (int i=0;i<5;i++){
            Cell deliveryTimeCell = row2.getCell(i);

            CellStyle styleMain = workbook.createCellStyle();
            if (i==4){
                styleMain.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            }else {
                styleMain.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());

            }
            Font font = workbook.createFont();
            //true为加粗，默认为不加粗
            font.setBold(true);
            //设置字体颜色，颜色和上述的颜色对照表是一样的
            font.setColor(IndexedColors.WHITE.getIndex());
            //将字体样式设置到单元格样式中
            styleMain.setFont(font);

            styleMain.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleMain.setAlignment(HorizontalAlignment.CENTER);
            styleMain.setVerticalAlignment(VerticalAlignment.CENTER);
//        CellStyle style =  workbook.createCellStyle();
//        style.setFillPattern(HSSFColor.HSSFColorPredefined.valueOf(""));
//        style.setFillForegroundColor(IndexedColors.RED.getIndex());
            deliveryTimeCell.setCellStyle(styleMain);
        }

        //获取第二行数据
        Row row3 =sheet.getRow(1);
        for (int x=4;x<6;x++) {
            Cell deliveryTimeCell1 = row3.getCell(x);
            CellStyle styleMain1 = workbook.createCellStyle();
            styleMain1.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            Font font1 = workbook.createFont();
            //true为加粗，默认为不加粗
            font1.setBold(true);
            //设置字体颜色，颜色和上述的颜色对照表是一样的
            font1.setColor(IndexedColors.WHITE.getIndex());
            //将字体样式设置到单元格样式中
            styleMain1.setFont(font1);

            styleMain1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleMain1.setAlignment(HorizontalAlignment.CENTER);
            styleMain1.setVerticalAlignment(VerticalAlignment.CENTER);
            deliveryTimeCell1.setCellStyle(styleMain1);
        }

        try {
            String fileName="采购单异常数据"+System.currentTimeMillis();
            URLEncoder.encode(fileName, "UTF-8");
            //response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xls");

            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");

            ServletOutputStream outStream = null;
            try {
                outStream = response.getOutputStream();
                workbook.write(outStream);
                outStream.flush();
            } finally {
                outStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       }

    }



    /**
     * 新增采购单模块
     */
    @PreAuthorize("@ss.hasPermi('Purchase:Purchase:add')")
    @Log(title = "采购单模块", businessType = BusinessType.INSERT)
    @PostMapping("addOrUpdate")
    @ApiOperation(value = "新增/修改采购单", notes = "新增采购单模块/提交触发创建入库单")
    public R addOrUpdate(@RequestBody PurchaseAddDTO purchase) {
        return toOk(purchaseService.insertPurchaseBatch(purchase));
    }

    /**
     * 删除采购单模块
     */
    @PreAuthorize("@ss.hasPermi('Purchase:Purchase:remove')")
    @Log(title = "采购单模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除采购单模块", notes = "删除采购单模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(purchaseService.deletePurchaseByIds(ids));
    }

    /**
     * 新增采购单模块
     */
    @PreAuthorize("@ss.hasPermi('Purchase:Purchase:delete')")
    @Log(title = "采购单模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/storage/cancel/byWarehouseNo/{warehouseNo}")
    @ApiImplicitParam(name = "warehouseNo", type = "String", value = "入库单号")
    @ApiOperation(value = "取消采购单入库", notes = "取消采购单入库 回调, 通过入库单id取消创建的采购单里面入库的请求数据")
    public R cancelByWarehouseNo(@PathVariable("warehouseNo") String warehouseNo) {
        purchaseService.cancelByWarehouseNo(warehouseNo);
        return R.ok();
    }


    /**
     * 转运-提交
     *
     * @param transportWarehousingAddDTO
     * @return
     */
    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:add')")
    @PostMapping(value = "transport/warehousing")
    @ApiOperation(value = "转运入库-提交")
    public R transportWarehousingSubmit(@RequestBody TransportWarehousingAddDTO transportWarehousingAddDTO) {
        return R.ok(purchaseService.transportWarehousingSubmit(transportWarehousingAddDTO));
    }
}
