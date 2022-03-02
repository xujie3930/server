package com.szmsd.returnex.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.plugin.HandlerContext;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.returnex.dto.ReturnExpressAddDTO;
import com.szmsd.returnex.dto.ReturnExpressListQueryDTO;
import com.szmsd.returnex.dto.ReturnExpressServiceAddDTO;
import com.szmsd.returnex.service.IReturnExpressService;
import com.szmsd.returnex.vo.ReturnExpressClientListVO;
import com.szmsd.returnex.vo.ReturnExpressListVO;
import com.szmsd.returnex.vo.ReturnExpressVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: ReturnExpressClientController
 * @Description: ReturnExpressController
 * @Author: 11
 * @Date: 2021/3/26 11:42
 */
@Api(tags = {"退货服务-客户端"})
@RestController
@RequestMapping("/client/return/express")
public class ReturnExpressClientController extends BaseController {

    @Resource
    private IReturnExpressService returnExpressService;

    /**
     * 新增退件单-生成预报单号
     *
     * @return 返回结果
     */
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:add')")
    @GetMapping("/createExpectedNo")
    @ApiOperation(value = "新增退件单-生成预报单号")
    public R<String> createExpectedNo() {
        return R.ok(returnExpressService.createExpectedNo());
    }

    /**
     * 新建退件单
     * 用户新增退件单，本地化数据，并推送给WMS
     *
     * @param returnExpressAddDTO 新增
     * @return 返回结果
     */
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:add')")
    @PostMapping("/add")
    @Log(title = "退货服务模块", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增退件单-创建退报单")
    public R add(@Validated @RequestBody ReturnExpressAddDTO returnExpressAddDTO) {
        return toOk(returnExpressService.insertReturnExpressDetail(returnExpressAddDTO));
    }

    /**
     * 退件单列表 - 分页
     *
     * @param queryDto 查询条件
     * @return 返回结果
     */
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:list')")
    @GetMapping("/page")
    @AutoValue
    @ApiOperation(value = "退件单列表 - 分页")
    public TableDataInfo<ReturnExpressListVO> page(@Validated ReturnExpressListQueryDTO queryDto) {
        startPage();
        List<ReturnExpressListVO> returnExpressListVOS = returnExpressService.selectClientReturnOrderList(queryDto);
        return getDataTable(returnExpressListVOS);
    }

    /**
     * 更新退件单信息
     *
     * @param expressUpdateDTO 更新条件
     * @return 返回结果
     */
    @AutoValue
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:update')")
    @PostMapping("/export")
    @Log(title = "退货服务模块", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "导出")
    public void export(HttpServletResponse httpServerResponse, @RequestBody @Validated ReturnExpressListQueryDTO queryDto) {
        List<ReturnExpressListVO> list = returnExpressService.selectClientReturnOrderList(queryDto);
        HandlerContext<List<ReturnExpressListVO>> objectHandlerContext = new HandlerContext<>(list);
        objectHandlerContext.handlerValue();
        List<ReturnExpressClientListVO> exportList = list.stream().map(x -> {
            ReturnExpressClientListVO returnExpressClientListVO = new ReturnExpressClientListVO();
            BeanUtils.copyProperties(x, returnExpressClientListVO);
            return returnExpressClientListVO;
        }).collect(Collectors.toList());
        ExcelUtil<ReturnExpressClientListVO> util = new ExcelUtil<>(ReturnExpressClientListVO.class);
        util.exportExcel(httpServerResponse, exportList, "退货记录-" + LocalDate.now());
    }

    /**
     * 更新退件单信息
     *
     * @param expressUpdateDTO 更新条件
     * @return 返回结果
     */
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:update')")
    @PostMapping("/update")
    @Log(title = "退货服务模块", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "更新退件单信息 指定sku的处理方式")
    public R update(@RequestBody ReturnExpressServiceAddDTO expressUpdateDTO) {
        return toOk(returnExpressService.updateExpressInfo(expressUpdateDTO));
    }

    /**
     * 更新退件单信息
     *
     * @param multipartFile 更新条件
     * @return 返回结果
     */
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:update')")
    @PostMapping("/importByTemplate")
    @Log(title = "退货服务模块", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "导入")
    public R<List<String>> importByTemplate(@RequestPart("file") MultipartFile multipartFile) {
        return R.ok(returnExpressService.importByTemplateClient(multipartFile));
    }

    /**
     * 更新退件单信息
     *
     * @param expressUpdateDTO 更新条件
     * @return 返回结果
     */
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:update')")
    @PostMapping("/exportTemplate")
    @Log(title = "退货服务模块", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "导出模板")
    public void exportTemplate(HttpServletResponse response) {
        ClassPathResource classPathResource = new ClassPathResource("/template/退货处理模板.xlsx");
        try (InputStream inputStream = classPathResource.getInputStream();
             ServletOutputStream outputStream = response.getOutputStream()) {

            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            String excelName = URLEncoder.encode("退货处理模板", "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + excelName + ".xls");
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取退件单信息详情
     *
     * @param id
     * @return
     */
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:query')")
    @GetMapping("/getInfo/{id}")
    @ApiOperation(value = "获取退件单信息详情")
    public R<ReturnExpressVO> getInfo(@PathVariable(value = "id") Long id) {
        return R.ok(returnExpressService.getInfo(id));
    }
}
