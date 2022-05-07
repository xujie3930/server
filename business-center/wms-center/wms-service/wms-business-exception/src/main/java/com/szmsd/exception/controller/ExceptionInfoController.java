package com.szmsd.exception.controller;

import cn.hutool.core.io.IoUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.ExcelUtils;
import com.szmsd.common.core.utils.QueryPage;
import com.szmsd.common.core.utils.SpringUtils;
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
import com.szmsd.exception.exported.DefaultSyncReadListener;
import com.szmsd.exception.exported.ExceptionInfoExportContext;
import com.szmsd.exception.exported.ExceptionInfoExportQueryPage;
import com.szmsd.exception.service.IExceptionInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询模块列表", notes = "查询模块列表")
    @AutoValue
    public TableDataInfo list(ExceptionInfoQueryDto dto) {
        startPage();
        List<ExceptionInfo> list = exceptionInfoService.selectExceptionInfoPage(dto);
        return getDataTable(list);
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
            dto.setSellerCode(sellerCode);
            // 查询出库类型数据
            Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("085");
            ExceptionInfoExportContext exportContext = new ExceptionInfoExportContext();
            exportContext.setStateCacheAdapter(listMap.get("085"));
            QueryDto queryDto1 = new QueryDto();
            queryDto1.setPageNum(1);
            queryDto1.setPageSize(300);
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

    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:importAgainTrackingNoTemplate')")
    @GetMapping("/importAgainTrackingNoTemplate")
    @ApiOperation(value = "导入重新获取挂号导入模板")
    public void importAgainTrackingNoTemplate(HttpServletResponse response) {
        String filePath = "/temp/exception_export_template.xlsx";
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
        this.downloadTemplate(response, filePath, fileName, "xlsx");
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
    public R<Map<String, Object>> importAgainTrackingNo(HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        AssertUtil.notNull(file, "上传文件不存在");
        try {
            DefaultSyncReadListener<ExceptionInfoExportDto> syncReadListener = new DefaultSyncReadListener<>();
            ExcelReaderBuilder excelReaderBuilder = EasyExcelFactory.read(file.getInputStream(), ExceptionInfoExportDto.class, syncReadListener);
            ExcelReaderSheetBuilder excelReaderSheetBuilder = excelReaderBuilder.sheet(0);
            excelReaderSheetBuilder.build().setHeadRowNumber(1);
            excelReaderSheetBuilder.doRead();
            List<ExceptionInfoExportDto> list = syncReadListener.getList();
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
                                if (exceptionInfoService.importAgainTrackingNo(dto, countryCode)) {
                                    successSize.incrementAndGet();
                                } else {
                                    errorList.add("第" + (finalI + 1) + "行，" + dto.getExceptionNo() + "操作失败，不符合条件");
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
                        try {
                            if (exceptionInfoService.importAgainTrackingNo(dto, countryCode)) {
                                successSize.incrementAndGet();
                            } else {
                                errorList.add("第" + (i + 1) + "行，" + dto.getExceptionNo() + "操作失败，不符合条件");
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

    /**
     * 修改模块
     */
    @PreAuthorize("@ss.hasPermi('ExceptionInfo:ExceptionInfo:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改模块", notes = "修改模块")
    public R edit(@RequestBody ExceptionInfoDto exceptionInfo) {
        return toOk(exceptionInfoService.updateExceptionInfo(exceptionInfo));
    }

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
