package com.szmsd.delivery.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.delivery.domain.DelOutboundCompleted;
import com.szmsd.delivery.domain.DelOutboundThirdParty;
import com.szmsd.delivery.dto.DelQueryServiceDto;
import com.szmsd.delivery.dto.DelQueryServiceExc;
import com.szmsd.delivery.dto.DelQueryServiceImport;
import com.szmsd.delivery.enums.DelOutboundCompletedStateEnum;
import com.szmsd.delivery.service.IDelOutboundCompletedService;
import com.szmsd.delivery.service.IDelOutboundThirdPartyService;
import com.szmsd.delivery.vo.DelOutboundThirdPartyenVO;
import com.szmsd.delivery.vo.DelOutboundThirdPartyzhVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * WMS推送 前端控制器
 * </p>
 *
 * @author asd
 * @since 2021-04-06
 */


@Api(tags = {"WMS推送"})
@RestController
@RequestMapping("/del-outbound-thirdParty")
public class DelOutboundThirdPartyController extends BaseController {

    @Resource
    private IDelOutboundThirdPartyService delOutboundThirdPartyService;

    /**
     * 查询出库单完成记录模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundThirdParty:DelOutboundThirdParty:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询WMS轮询的订单列表", notes = "查询WMS轮询的订单列表")
    public TableDataInfo list(DelOutboundThirdParty delOutboundThirdParty) {
        startPage();
        List<DelOutboundThirdParty> list = delOutboundThirdPartyService.selectDelOutboundThirdPartyList(delOutboundThirdParty);
        return getDataTable(list);
    }


    /**
     * 导出查件服务模块列表
     */
    @Log(title = "wms推送导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation(value = "wms推送导出",notes = "wms推送导出")
    public void export(HttpServletResponse response,@RequestBody DelOutboundThirdParty delOutboundThirdParty) throws IOException {
        String len=getLen();
        List<DelOutboundThirdParty> list = delOutboundThirdPartyService.selectDelOutboundThirdPartyList(delOutboundThirdParty);
        ExportParams params = new ExportParams();
        //params.setTitle("查件服务");
        List<DelOutboundThirdPartyzhVO> list1=new ArrayList<>();
        List<DelOutboundThirdPartyenVO> list2=new ArrayList<>();
        String fileName=null;
        Workbook workbook=null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (len.equals("zh")){
            list1= BeanMapperUtil.mapList(list, DelOutboundThirdPartyzhVO.class);
            list1.forEach(x->{
               x.setCreateTimes(sdf.format(x.getCreateTime()));
            });
            workbook=  ExcelExportUtil.exportExcel(params, DelOutboundThirdPartyzhVO.class, list1);
            fileName  ="WMS推送"+System.currentTimeMillis();

        }else if (len.equals("en")){
            list2= BeanMapperUtil.mapList(list, DelOutboundThirdPartyenVO.class);
            list2.forEach(x->{
                x.setCreateTimes(sdf.format(x.getCreateTime()));
            });
            workbook=  ExcelExportUtil.exportExcel(params, DelOutboundThirdPartyenVO.class, list2);
            fileName  ="WMSPush"+System.currentTimeMillis();
        }







        Sheet sheet= workbook.getSheet("sheet0");

        //获取第一行数据
        Row row2 =sheet.getRow(0);

        for (int i=0;i<4;i++){
            Cell deliveryTimeCell = row2.getCell(i);

            CellStyle styleMain = workbook.createCellStyle();

            styleMain.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
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


        try {

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


    /**
     * 新增出库单完成记录模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundThirdParty:DelOutboundThirdParty:pushAgain')")
    @Log(title = "出库单完成记录模块", businessType = BusinessType.INSERT)
    @PostMapping("pushAgain")
    @ApiOperation(value = "重推数据", notes = "重推数据")
    public R pushAgain(@RequestBody List<Long> ids) {

        if(ids == null || ids.isEmpty()){
            return R.failed("参数不能为空");
        }
        List<DelOutboundThirdParty> list = delOutboundThirdPartyService.listByIds(ids);
        List<DelOutboundThirdParty> newList = new ArrayList<>();
        for(DelOutboundThirdParty vo: list){
            if(DelOutboundCompletedStateEnum.FAIL.getCode().equals(vo.getState())){
                vo.setNextHandleTime(new Date());
                vo.setHandleSize(0);
                newList.add(vo);
            }
        }
        if(newList.size() > 0){
            delOutboundThirdPartyService.updateBatchById(newList);
            return R.ok();
        } else{
            return R.failed("没有符合条件的数据进行重推");
        }
    }


}
