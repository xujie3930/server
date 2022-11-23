package com.szmsd.exception.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.io.IoUtil;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.bas.domain.BasFile;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.ExcelUtils;
import com.szmsd.common.core.utils.QueryPage;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.exception.domain.ExceptionInfo;
import com.szmsd.exception.dto.*;
import com.szmsd.exception.enums.StateSubEnum;
import com.szmsd.exception.exported.ExceptionInfoExportContext;
import com.szmsd.exception.exported.ExceptionInfoExportQueryPage;
import com.szmsd.exception.mapper.BasExcetionFileMapper;
import com.szmsd.exception.mapper.ExceptionInfoMapper;
import com.szmsd.exception.service.IExceptionInfoService;
import com.szmsd.exception.task.ExceptionEasyPoiExportTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author l
 * @since 2021-03-30
 */
@Api(tags = {"异常信息"})
@RestController
@RequestMapping("/exception/info")
public class ExceptionInfoController extends BaseController {

    @Resource
    private IExceptionInfoService exceptionInfoService;
    @Autowired
    private BasSubClientService basSubClientService;
    @SuppressWarnings({"all"})
    @Autowired
    private BasRegionFeignService basRegionFeignService;
    @Autowired
    private ExceptionInfoMapper exceptionInfoMapper;
    @Autowired
    private BasExcetionFileMapper basExcetionFileMapper;

    @Value("${filepaths}")
    private String filepath;

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询模块列表", notes = "查询模块列表")
    @AutoValue
    public TableDataInfo list(ExceptionInfoQueryDto dto) {

        return exceptionInfoService.selectExceptionInfoPage(dto);

    }

    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:list')")
    @PostMapping("/count")
    @ApiOperation(value = "查询模块列表", notes = "查询模块列表")
    public R<Integer> count(@RequestBody String sellerCode) {
        QueryWrapper<ExceptionInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seller_code", sellerCode);
        queryWrapper.eq("state", StateSubEnum.DAICHULI.getCode());
        return R.ok(exceptionInfoService.count(queryWrapper));
    }

    /**
     * 导出模块列表
     */
    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:export')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出模块列表", notes = "导出模块列表")
    public void export(HttpServletResponse response, ExceptionInfoQueryDto dto) throws IOException {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (null == loginUser) {
                throw new CommonException("500", "非法的操作");
            }
            // 获取登录用户的客户编码
            String sellerCode = loginUser.getSellerCode();
            //dto.setSellerCode(sellerCode);
            // 查询出库类型数据
            Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("085");
            ExceptionInfoExportContext exportContext = new ExceptionInfoExportContext();
            exportContext.setStateCacheAdapter(listMap.get("085"));
            QueryDto queryDto1 = new QueryDto();
            queryDto1.setPageNum(1);
            queryDto1.setPageSize(80000);
            QueryPage<ExceptionInfoExportDto> queryPage = new ExceptionInfoExportQueryPage(dto, queryDto1, exportContext, this.exceptionInfoService);
            String pathName = "/temp/exception_export_template.xlsx";
            org.springframework.core.io.Resource resource = new ClassPathResource(pathName);
            InputStream inputStream = resource.getInputStream();
            ExcelUtils.export(response, inputStream, null, ExcelUtils.ExportExcel.build("异常通知中心_异常导出", null, null, new ExcelUtils.ExportSheet<ExceptionInfoExportDto>() {
                @Override
                public String sheetName() {
                    return "";
                }

                @Override
                public Class<ExceptionInfoExportDto> classType() {
                    return ExceptionInfoExportDto.class;
                }

                @Override
                public QueryPage<ExceptionInfoExportDto> query(ExcelUtils.ExportContext exportContext) {
                    return queryPage;
                }
            }));
        } catch (Exception e) {
            log.error("导出异常:" + e.getMessage(), e);
            throw new CommonException("999", "导出异常：" + e.getMessage());
        }
    }



    /**
     * 导出模块列表(导入一对多合并单元格)
     */
    //@PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:export')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/exportus")
    @ApiOperation(value = "导出模块列表", notes = "导出模块列表")
    public void exportus(HttpServletResponse response, ExceptionInfoQueryDto dto) throws IOException, InterruptedException {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CommonException("500", "非法的操作");
        }




        // 获取登录用户的客户编码
        String sellerCode = loginUser.getSellerCode();
        //dto.setSellerCode(sellerCode);

        if (dto.getType()==0) {
            if (dto.getSellerCode()!=null){
                List<String> list= Arrays.asList(dto.getSellerCode().split(","));
                dto.setSellerCodes(list);
            }
        }else if (dto.getType()==1) {
            //pc端
            List<String> sellerCodeList = null;
            if (null != loginUser && !loginUser.getUsername().equals("admin")) {
                String username = loginUser.getUsername();
                sellerCodeList = exceptionInfoMapper.selectsellerCode(username);

                if (sellerCodeList.size() > 0) {
                    dto.setSellerCodes(sellerCodeList);

                }
                if (sellerCodeList.size() == 0) {
                    sellerCodeList.add("");
                    dto.setSellerCodes(sellerCodeList);
                }
            }
        }

        Integer   ExceptionInfoTotal=exceptionInfoService.selectExceptionInfoQuery(dto);



        // 查询出库类型数据
        Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("085");
        ExceptionInfoExportContext exportContext = new ExceptionInfoExportContext();
        exportContext.setStateCacheAdapter(listMap.get("085"));
        QueryDto queryDto1 = new QueryDto();
        if (ExceptionInfoTotal>500){
            String filepath=this.filepath;
            Integer pageSize = 100000;

            // 数据分页==导出excel文件数量
            int pageTotal = ExceptionInfoTotal % pageSize == 0 ? ExceptionInfoTotal / pageSize : ExceptionInfoTotal / pageSize + 1;
            log.info("导出数据总量：{}条, 预计导出文件数量：{}件", ExceptionInfoTotal, pageTotal);
            CountDownLatch countDownLatch = new CountDownLatch(pageTotal);
            long start = System.currentTimeMillis();

            for (int i = 1; i <= pageTotal; i++) {
                queryDto1.setPageNum(i);
                queryDto1.setPageSize(pageSize);
                Date date =new Date();
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
                QueryPage<ExceptionInfoExportDto> queryPage = new ExceptionInfoExportQueryPage(dto, queryDto1, exportContext, this.exceptionInfoService);
                List<ExceptionInfoExportDto> list = queryPage.getPage();
                list.forEach(x -> {
                    if (x.getOrderTypeName().equals("出库单")) {
                        x.setExceptionInfoDetailExportDtoList(exceptionInfoService.selectExceptionInfoDetailExport(x.getOrderNo()));

                    }
                });
                String fileName = "异常信息明细-" +loginUser.getUsername()+"-"+ simpleDateFormat.format(date);
                BasFile basFile = new BasFile();
                basFile.setState("0");
                basFile.setFileRoute(filepath);
                basFile.setCreateBy(SecurityUtils.getUsername());
                basFile.setFileName(fileName + ".xls");
                basFile.setModularType(1);
                basFile.setModularNameZh("异常中心导出");
                basFile.setModularNameEn("ExceptionExport");
                basExcetionFileMapper.insertSelective(basFile);

                ExceptionEasyPoiExportTask<ExceptionInfoExportDto> ExceptionInfoExTask = new ExceptionEasyPoiExportTask<ExceptionInfoExportDto>()
                        .setExportParams(new ExportParams(fileName, "出库单详情(" + ((i - 1) * pageSize) + "-" + (Math.min(i * pageSize, ExceptionInfoTotal)) + ")", ExcelType.XSSF))
                        .setData(list)
                        .setClazz(ExceptionInfoExportDto.class)
                        .setFilepath(filepath)
                        .setCountDownLatch(countDownLatch)
                        .setExceptionInfoQueryDto(dto)
                        .setFileId(basFile.getId());

                basFile.setState("1");
                basExcetionFileMapper.updateByPrimaryKeySelective(basFile);
                //threadPoolTaskExecutor.execute(delOutboundExportExTask);
                new Thread(ExceptionInfoExTask, "export-" + i).start();

            }
            countDownLatch.await();
            log.info("所有导出任务完成，总计耗时：{}ms", System.currentTimeMillis() - start);


        }else if (ExceptionInfoTotal<=500) {

            queryDto1.setPageNum(1);
            queryDto1.setPageSize(500);
            QueryPage<ExceptionInfoExportDto> queryPage = new ExceptionInfoExportQueryPage(dto, queryDto1, exportContext, this.exceptionInfoService);
            List<ExceptionInfoExportDto> list = queryPage.getPage();
            list.forEach(x -> {
                if (x.getOrderTypeName().equals("出库单")) {
                    x.setExceptionInfoDetailExportDtoList(exceptionInfoService.selectExceptionInfoDetailExport(x.getOrderNo()));
                }
            });
            ExportParams params = new ExportParams();
//        params.setTitle("异常通知中心_异常导出");
            int a = 0;
            Workbook workbook = null;
            if (dto.getType() == 0) {
                List<ExceptionInfoExportCustomerDto> exceptionInfoExportCustomerDtos = BeanMapperUtil.mapList(list, ExceptionInfoExportCustomerDto.class);
                workbook = ExcelExportUtil.exportExcel(params, ExceptionInfoExportCustomerDto.class, exceptionInfoExportCustomerDtos);
                a = 1;

            } else if (dto.getType() == 1) {
                workbook = ExcelExportUtil.exportExcel(params, ExceptionInfoExportDto.class, list);

            }


            Sheet sheet = workbook.getSheet("sheet0");

            //获取第一行数据
            Row row2 = sheet.getRow(0);

            for (int i = 0; i < 19 - a; i++) {
                Cell deliveryTimeCell = row2.getCell(i);

                CellStyle styleMain = workbook.createCellStyle();
                if (i == 18 - a) {
                    styleMain.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
                } else {
                    styleMain.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());

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
            Row row3 = sheet.getRow(1);
            for (int x = 18 - a; x < 23 - a; x++) {

                Cell deliveryTimeCell1 = row3.getCell(x);
                CellStyle styleMain1 = workbook.createCellStyle();
                styleMain1.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
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
            //总行数
//        int rowNum=sheet.getLastRowNum()+2;
//        for (int j=2;j<rowNum;j++) {
//            Row row4 = sheet.getRow(j);
//            if (row4!=null) {
//                for (int x = 0; x < 23-a; x++) {
//
//
//                    Cell deliveryTimeCell1 = row4.getCell(x);
//                    if (deliveryTimeCell1 != null) {
//                        CellStyle styleMain1 = workbook.createCellStyle();
//                        styleMain1.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
//                         styleMain1.setBorderBottom(BorderStyle.THIN);//下边框
//                         styleMain1.setBorderLeft(BorderStyle.THIN);//左边框
//                         styleMain1.setBorderTop(BorderStyle.THIN);//上边框
//                         styleMain1.setBorderRight(BorderStyle.THIN);//右边框
//                        //设置边框颜色黑色
//                        styleMain1.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
//                        styleMain1.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
//                        styleMain1.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
//                        styleMain1.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
//
//                        styleMain1.setAlignment(HorizontalAlignment.CENTER);
////                        if (x==18-a){
////                            styleMain1.setHidden(true);
////                        }
//
////                        if (x==19-a) {
////
////                            styleMain1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
////                            styleMain1.setLocked(true);
////
////                        } else {
////                            styleMain1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
////                            styleMain1.setLocked(false);
////                        }
//
//                        deliveryTimeCell1.setCellStyle(styleMain1);
//                    }
//                }
//            }
//        }
//        sheet.protectSheet("123456");

            if (dto.getType() == 1) {
                sheet.setColumnHidden(18, true);
            } else {
                sheet.setColumnHidden(17, true);

            }
            try {
                String fileName = "异常通知中心_异常导出" + System.currentTimeMillis();
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

    @Log(title = "出库管理 - 导出查询", businessType = BusinessType.OTHER)
    @GetMapping("/exceptionExportselect")
    @ApiOperation(value = "出库管理 - 导出", position = 1600)
    @SneakyThrows
    public R exceptionExportselect(ExceptionInfoQueryDto dto) {
        try {

            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (null == loginUser) {
                throw new CommonException("500", "非法的操作");
            }




            // 获取登录用户的客户编码
            String sellerCode = loginUser.getSellerCode();
            //dto.setSellerCode(sellerCode);

            if (dto.getType()==0) {
                if (dto.getSellerCode()!=null){
                    List<String> list= Arrays.asList(dto.getSellerCode().split(","));
                    dto.setSellerCodes(list);
                }
            }else if (dto.getType()==1) {
                //pc端
                List<String> sellerCodeList = null;
                if (null != loginUser && !loginUser.getUsername().equals("admin")) {
                    String username = loginUser.getUsername();
                    sellerCodeList = exceptionInfoMapper.selectsellerCode(username);

                    if (sellerCodeList.size() > 0) {
                        dto.setSellerCodes(sellerCodeList);

                    }
                    if (sellerCodeList.size() == 0) {
                        sellerCodeList.add("");
                        dto.setSellerCodes(sellerCodeList);
                    }
                }
            }

            Integer   ExceptionInfoTotal=exceptionInfoService.selectExceptionInfoQuery(dto);

            return R.ok(ExceptionInfoTotal);

        } catch (Exception e) {
            log.error("查询参数:" + e.getMessage(), e);
            return R.failed("操作失败");
        }
    }

    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:importAgainTrackingNoTemplate')")
    @GetMapping("/importAgainTrackingNoTemplate")
    @ApiOperation(value = "导入重新获取挂号导入模板")
    public void importAgainTrackingNoTemplate(HttpServletResponse response) {
        //String filePath = "/temp/exception_export_template.xlsx";
        String filePath = "/temp/exception_export_templatesa.xls";

        String fileName = "异常通知中心_异常导出";
        this.downloadTemplate(response, filePath, fileName);
    }

    /**
     * 下载模板
     *
     * @param response response
     * @param filePath 文件存放路径，${server.tomcat.basedir}配置的目录和resources目录下
     * @param fileName 文件名称
     */
    private void downloadTemplate(HttpServletResponse response, String filePath, String fileName) {
        //this.downloadTemplate(response, filePath, fileName, "xlsx");
        this.downloadTemplate(response, filePath, fileName, "xls");
    }

    /**
     * 下载模板
     *
     * @param response response
     * @param filePath 文件存放路径，${server.tomcat.basedir}配置的目录和resources目录下
     * @param fileName 文件名称
     * @param ext      扩展名
     */
    private void downloadTemplate(HttpServletResponse response, String filePath, String fileName, String ext) {
        // 先去模板目录中获取模板
        // 模板目录中没有模板再从项目中获取模板
        String basedir = SpringUtils.getProperty("server.tomcat.basedir", "/u01/www/ck1/delivery");
        File file = new File(basedir + "/" + filePath);
        InputStream inputStream = null;
        ServletOutputStream outputStream = null;
        try {
            if (file.exists()) {
                inputStream = new FileInputStream(file);
                response.setHeader("File-Source", "local");
            } else {
                org.springframework.core.io.Resource resource = new ClassPathResource(filePath);
                inputStream = resource.getInputStream();
                response.setHeader("File-Source", "resource");
            }
            outputStream = response.getOutputStream();
            //response为HttpServletResponse对象
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //Loading plan.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            String efn = URLEncoder.encode(fileName, "utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + efn + "." + ext);
            IOUtils.copy(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new CommonException("400", "文件不存在，" + e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new CommonException("500", "文件流处理失败，" + e.getMessage());
        } finally {
            IoUtil.flush(outputStream);
            IoUtil.close(outputStream);
            IoUtil.close(inputStream);
        }
    }

    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:importAgainTrackingNo')")
    @PostMapping("/importAgainTrackingNo")
    @ApiOperation(value = "导入重新获取挂号")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R<Map<String, Object>> importAgainTrackingNo(@RequestPart("file") MultipartFile file) {
//        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
//        MultipartFile file = multipartHttpServletRequest.getFile("file");
        AssertUtil.notNull(file, "上传文件不存在");
        try {
//            DefaultSyncReadListener<ExceptionInfoExportDto> syncReadListener = new DefaultSyncReadListener<>();
//            ExcelReaderBuilder excelReaderBuilder = EasyExcelFactory.read(file.getInputStream(), ExceptionInfoExportDto.class, syncReadListener);
//            ExcelReaderSheetBuilder excelReaderSheetBuilder = excelReaderBuilder.sheet(0);
//            excelReaderSheetBuilder.build().setHeadRowNumber(1);
//            excelReaderSheetBuilder.doRead();
//            List<ExceptionInfoExportDto> list = syncReadListener.getList();

            //导入新的解析方法
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(2);
            List<ExceptionInfoExportDto> list = ExcelImportUtil.importExcel(file.getInputStream(),ExceptionInfoExportDto.class,params);

            Map<String, Object> map = new HashMap<>();
            if (CollectionUtils.isNotEmpty(list)) {
                int size = list.size();
                AtomicInteger successSize = new AtomicInteger();
                AtomicInteger failSize = new AtomicInteger();
                Map<String, String> countryMap = new HashMap<>();
                List<String> errorList = new ArrayList<>();
                if (size > 100) {
                    int availableProcessors = Runtime.getRuntime().availableProcessors();
                    CountDownLatch downLatch = new CountDownLatch(size);
                    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(availableProcessors + 1);
                    for (int i = 0; i < list.size(); i++) {
                        ExceptionInfoExportDto dto = list.get(i);
                        if (StringUtils.isEmpty(dto.getCountry())) {
                            errorList.add("第" + (i + 1) + "行，异常号不能为空");
                            failSize.incrementAndGet();
                            continue;
                        }
                        if (StringUtils.isEmpty(dto.getCountry())) {
                            errorList.add("第" + (i + 1) + "行，" + dto.getExceptionNo() + "国家不能为空");
                            failSize.incrementAndGet();
                            continue;
                        }
                        String countryCode;
                        if (countryMap.containsKey(dto.getCountry())) {
                            countryCode = countryMap.get(dto.getCountry());
                        } else {
                            countryCode = getCountryCode(dto.getCountry());
                            countryMap.put(dto.getCountry(), countryCode);
                        }
                        if (null == countryCode) {
                            errorList.add("第" + (i + 1) + "行，" + dto.getExceptionNo() + "国家[" + dto.getCountry() + "]不存在");
                            failSize.incrementAndGet();
                            continue;
                        }
                        int finalI = i;
                        fixedThreadPool.execute(() -> {
                            try {
                                dto.setCountry(getCountryCodeS(countryCode));
                                if (exceptionInfoService.importAgainTrackingNo(dto, countryCode)) {
                                    successSize.incrementAndGet();
                                    if(dto.getOrderTypeName().equals("出库单")){
                                        if (dto.getExceptionInfoDetailExportDtoList().size()>0){
                                            exceptionInfoService.updateDelOutboundDetail(dto.getOrderNo(),dto.getExceptionInfoDetailExportDtoList());

                                        }

                                        if (dto.getIoss()!=null&&!dto.getIoss().equals("")){
                                            exceptionInfoService.updateDelOutboundIoss(dto);
                                        }

                                    }
                                } else {
                                    errorList.add("第" + (finalI + 1) + "行，" + dto.getExceptionNo() + "导入数据在异常表里面必须类型为待处理，单类型为出库单，异常类型可以为（出库-获取挂号失败，出库-超重件，出库-超规格件）");
                                    failSize.incrementAndGet();
                                }
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                                errorList.add("第" + (finalI + 1) + "行，" + dto.getExceptionNo() + "操作失败，" + e.getMessage());
                                failSize.incrementAndGet();
                            } finally {
                                downLatch.countDown();
                            }
                        });
                    }
                    try {
                        downLatch.await();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    } finally {
                        fixedThreadPool.shutdown();
                    }
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        ExceptionInfoExportDto dto = list.get(i);
                        if (StringUtils.isEmpty(dto.getExceptionNo())) {
                            errorList.add("第" + (i + 1) + "行，异常号不能为空");
                            failSize.incrementAndGet();
                            continue;
                        }
                        if (StringUtils.isEmpty(dto.getCountry())) {
                            errorList.add("第" + (i + 1) + "行，" + dto.getExceptionNo() + "国家不能为空");
                            failSize.incrementAndGet();
                            continue;
                        }
                        String countryCode;
                        if (countryMap.containsKey(dto.getCountry())) {
                            countryCode = countryMap.get(dto.getCountry());
                        } else {
                            countryCode = getCountryCode(dto.getCountry());
                            countryMap.put(dto.getCountry(), countryCode);
                        }
                        if (null == countryCode) {
                            errorList.add("第" + (i + 1) + "行，" + dto.getExceptionNo() + "国家[" + dto.getCountry() + "]不存在");
                            failSize.incrementAndGet();
                            continue;
                        }
                        try {
                            dto.setCountry(getCountryCodeS(countryCode));
                            if (exceptionInfoService.importAgainTrackingNo(dto, countryCode)) {
                                successSize.incrementAndGet();
                                if(dto.getOrderTypeName().equals("出库单")){
                                    if (dto.getExceptionInfoDetailExportDtoList().size()>0){
                                        exceptionInfoService.updateDelOutboundDetail(dto.getOrderNo(),dto.getExceptionInfoDetailExportDtoList());

                                    }

                                    if (dto.getIoss()!=null&&!dto.getIoss().equals("")){
                                        exceptionInfoService.updateDelOutboundIoss(dto);
                                    }

                                }
                            } else {
                                errorList.add("第" + (i + 1) + "行，" + dto.getExceptionNo() + "导入数据在异常表里面必须类型为待处理，单类型为出库单，异常类型可以为（出库-获取挂号失败，出库-超重件，出库-超规格件）");
                                failSize.incrementAndGet();
                            }

                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            errorList.add("第" + (i + 1) + "行，" + dto.getExceptionNo() + "操作失败，" + e.getMessage());
                            failSize.incrementAndGet();
                        }
                    }
                }
                map.put("size", size);
                map.put("successSize", successSize.intValue());
                map.put("failSize", failSize.intValue());
                map.put("msg", "导入完成");
                map.put("errorList", errorList);
            } else {
                map.put("msg", "导入数据为空");
            }
            return R.ok(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.failed(e.getMessage());
        }
    }


    private String getCountryCode(String country) {
        R<BasRegionSelectListVO> listVOR = this.basRegionFeignService.queryByCountryName(country);
        BasRegionSelectListVO vo = R.getDataAndException(listVOR);
        if (null != vo) {

            return vo.getAddressCode();
        }
        return null;
    }

    private String getCountryCodeS(String country) {
        R<BasRegionSelectListVO> listVOR = this.basRegionFeignService.queryByCountryName(country);
        BasRegionSelectListVO vo = R.getDataAndException(listVOR);
        if (null != vo) {
            //汪俊余要改的
            return vo.getEnName();
        }
        return null;
    }

    /**
     * 获取模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取模块详细信息", notes = "获取模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(exceptionInfoService.selectExceptionInfoById(id));
    }

    /**
     * 新增模块
     */
    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增模块", notes = "新增模块")
    public R add(@RequestBody NewExceptionRequest newExceptionRequest) {
        exceptionInfoService.insertExceptionInfo(newExceptionRequest);
        return R.ok();
    }

    /**
     * 新增模块
     */
    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("process")
    @ApiOperation(value = "处理模块", notes = "处理模块")
    public R process(@RequestBody ProcessExceptionRequest processExceptionRequest) {
        exceptionInfoService.processExceptionInfo(processExceptionRequest);
        return R.ok();
    }


    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("processByOrderNo")
    @ApiOperation(value = "处理模块-订单号", notes = "处理模块-订单号")
    public R processByOrderNo(@RequestBody ProcessExceptionOrderRequest processExceptionRequest) {
        exceptionInfoService.processByOrderNo(processExceptionRequest);
        return R.ok();
    }

    /**
     * 修改模块
     */
    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改模块", notes = "修改模块")
    public R edit(@RequestBody List<ExceptionInfoDto> exceptionInfo) {
        return toOk(exceptionInfoService.updateExceptionInfo(exceptionInfo));
    }

//    /**
//     * 修改模块
//     */
//    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:edit')")
//    @Log(title = "模块", businessType = BusinessType.UPDATE)
//    @PutMapping("edit")
//    @ApiOperation(value = " 修改模块", notes = "修改模块")
//    public R edit(@RequestBody ExceptionInfoDto exceptionInfo) {
//        return toOk(exceptionInfoService.updateExceptionInfo(exceptionInfo));
//    }



    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除模块", notes = "删除模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(exceptionInfoService.deleteExceptionInfoByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:againTrackingNo')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PostMapping("/againTrackingNo")
    @ApiOperation(value = "重新获取挂号", notes = "重新获取挂号")
    public R againTrackingNo(@RequestBody ExceptionDelOutboundAgainTrackingNoDto dto) {
        return R.ok(this.exceptionInfoService.againTrackingNo(dto));
    }

    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:ignore')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PostMapping("/ignore")
    @ApiOperation(value = "忽略异常", notes = "根据出库单号忽略异常")
    public R<Integer> ignore(@RequestBody ExceptionInfoDto exceptionInfo) {
        return R.ok(this.exceptionInfoService.ignore(exceptionInfo));
    }
}
