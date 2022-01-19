package com.szmsd.returnex.controller;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.event.SyncReadListener;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.plugin.HandlerContext;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.finance.dto.RefundRequestDTO;
import com.szmsd.finance.vo.RefundRequestListVO;
import com.szmsd.returnex.dto.ReturnExpressAddDTO;
import com.szmsd.returnex.dto.ReturnExpressAssignDTO;
import com.szmsd.returnex.dto.ReturnExpressListQueryDTO;
import com.szmsd.returnex.dto.ReturnExpressServiceAddDTO;
import com.szmsd.returnex.service.IReturnExpressService;
import com.szmsd.returnex.vo.ReturnExpressListVO;
import com.szmsd.returnex.vo.ReturnExpressVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.concurrent.*;

/**
 * @ClassName: ReturnExpressServerController
 * @Description: ReturnExpressController
 * @Author: 11
 * @Date: 2021/3/26 11:42
 */
@Api(tags = {"退货服务-管理端"})
@RestController
@RequestMapping("/server/return/express")
public class ReturnExpressServerController extends BaseController {

    @Resource
    private IReturnExpressService returnExpressService;

    /**
     * 退件单列表 - 分页
     *
     * @param queryDto 查询条件
     * @return 返回结果
     */
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:list')")
    @AutoValue
    @GetMapping("/page")
    @ApiOperation(value = "退件单列表 - 分页")
    public TableDataInfo<ReturnExpressListVO> page(@Validated ReturnExpressListQueryDTO queryDto) {
        startPage();
        return getDataTable(returnExpressService.selectReturnOrderList(queryDto));
    }

    /**
     * 无名件管理列表 - 分页
     *
     * @param queryDto 查询条件
     * @return 返回结果
     */
    @AutoValue
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:list')")
    @GetMapping("/noUserBind/page")
    @ApiOperation(value = "无名件管理列表 - 分页")
    public TableDataInfo<ReturnExpressListVO> pageForNoUserBind(@Validated ReturnExpressListQueryDTO queryDto) {
        startPage();
        return getDataTable(returnExpressService.pageForNoUserBind(queryDto));
    }

    /**
     * 无名件批量指派客户
     *
     * @param expressAssignDTO 指派条件
     * @return 返回结果
     */
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:update')")
    @PostMapping("/noUserBind/assignUsers")
    @Log(title = "退货服务模块", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "无名件批量指派客户")
    public R assignUsersForNoUserBindBatch(@Validated @RequestBody ReturnExpressAssignDTO expressAssignDTO) {
        return toOk(returnExpressService.assignUsersForNoUserBindBatch(expressAssignDTO));
    }

    /**
     * 获取退件单信息详情
     *
     * @param id
     * @return
     */
    @GetMapping("/getInfo/{id}")
    @ApiOperation(value = "获取退件单信息详情")
    public R<ReturnExpressVO> getInfo(@PathVariable(value = "id") Long id) {
        return R.ok(returnExpressService.getInfo(id));
    }

    @PatchMapping("/updateTrackNo/byOutBoundNo/{outBoundNo}/{trackNo}")
    @ApiOperation(value = "更新运单跟踪号-根据出库单号", notes = "退件后重派创建新出库单,回调接口")
    public R<ReturnExpressVO> updateTrackNoByOutBoundNo(@PathVariable(value = "outBoundNo") String outBoundNo, @PathVariable(value = "trackNo") String trackNo) {
        returnExpressService.updateTrackNoByOutBoundNo(outBoundNo, trackNo);
        return R.ok();
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
    public R add(@Validated @RequestBody ReturnExpressServiceAddDTO returnExpressAddDTO) {
        return toOk(returnExpressService.insertReturnExpressDetail(returnExpressAddDTO));
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
    public R update(@Validated @RequestBody ReturnExpressServiceAddDTO expressUpdateDTO) {
        return toOk(returnExpressService.updateExpressInfo(expressUpdateDTO));
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
    public void export(HttpServletResponse httpServerResponse, @Validated ReturnExpressListQueryDTO queryDto) {
        List<ReturnExpressListVO> list = returnExpressService.selectReturnOrderList(queryDto);
        HandlerContext<List<ReturnExpressListVO>> objectHandlerContext = new HandlerContext<>(list);
        objectHandlerContext.handlerValue();
        ExcelUtil<ReturnExpressListVO> util = new ExcelUtil<>(ReturnExpressListVO.class);
        util.exportExcel(httpServerResponse, list, "退货记录-" + LocalDate.now());
    }

    /**
     * 更新退件单信息
     *
     * @param expressUpdateDTO 更新条件
     * @return 返回结果
     */
    @AutoValue
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:update')")
    @PostMapping("/exportTemplate")
    @Log(title = "退货服务模块", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "导出模板")
    public void exportTemplate(HttpServletResponse response) {
        ClassPathResource classPathResource = new ClassPathResource("/template/退货申请模板.xlsx");
        try (InputStream inputStream = classPathResource.getInputStream();
             ServletOutputStream outputStream = response.getOutputStream()) {

            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            String excelName = URLEncoder.encode("退货申请模板", "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + excelName + ".xls");
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ExcelUtil<ReturnExpressServiceAddDTO> util = new ExcelUtil<>(ReturnExpressServiceAddDTO.class);
        util.exportExcel(response, new ArrayList<>(), "退货记录-" + LocalDate.now());
    }

    /**
     * 更新退件单信息
     *
     * @param expressUpdateDTO 更新条件
     * @return 返回结果
     */
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:update')")
    @PostMapping("/importByTemplate")
    @Log(title = "退货服务模块", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "导入")
    public R<String> importByTemplate(@RequestPart("file") MultipartFile multipartFile) {
        returnExpressService.importByTemplate(multipartFile);
        return R.ok();
    }
}
