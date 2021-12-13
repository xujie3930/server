package com.szmsd.http.controller;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.dto.mapping.HtpWarehouseMappingDTO;
import com.szmsd.http.dto.mapping.HtpWarehouseMappingQueryDTO;
import com.szmsd.http.vo.HttpResponseVO;
import com.szmsd.http.vo.mapping.CkWarehouseMappingVO;
import com.szmsd.http.vo.mapping.HtpWarehouseMappingVO;
import org.apache.http.Header;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.szmsd.http.service.IHtpWarehouseMappingService;
import com.szmsd.http.domain.HtpWarehouseMapping;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.core.web.page.TableDataInfo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import com.szmsd.common.core.web.controller.BaseController;


/**
 * <p>
 * 仓库与仓库关联映射 前端控制器
 * </p>
 *
 * @author 11
 * @since 2021-12-13
 */


@Api(tags = {"仓库与仓库关联映射"})
@RestController
@RequestMapping("/htpWarehouseMapping")
public class HtpWarehouseMappingController extends BaseController {

    @Resource
    private IHtpWarehouseMappingService htpWarehouseMappingService;
    @Resource
    private RemoteInterfaceController remoteInterfaceController;

    /**
     * 查询仓库与仓库关联映射模块列表
     */
    @PreAuthorize("@ss.hasPermi('HtpWarehouseMapping:HtpWarehouseMapping:list')")
    @PostMapping("/ck1/list")
    @ApiOperation(value = "查询CK1 仓库列表", notes = "查询仓库与仓库关联映射模块列表")
    public R<List<HtpWarehouseMappingVO>> cklist() {
        HttpRequestDto dto = new HttpRequestDto();
        dto.setMethod(HttpMethod.GET);
        Map<String, String> head = new LinkedHashMap<>();
        head.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        // 设置用户head
        head.put(HttpHeaders.AUTHORIZATION, "Bearer OGE0M2UzNmItMzVhMy00MDY5LWJkMjgtMWIwYjQ4ZmQ3YmM0");
        dto.setHeaders(head);
        dto.setUri("http://openapi.ck1info.com/v1/warehouses");
        R<HttpResponseVO> rmi = remoteInterfaceController.rmi(dto);
        HttpResponseVO data = rmi.getData();
        byte[] body = data.getBody();
        try {
            String responseStr = new String(body, StandardCharsets.UTF_8);
            List<CkWarehouseMappingVO> htpWarehouseMappings = JSONObject.parseArray(responseStr, CkWarehouseMappingVO.class);
            List<HtpWarehouseMappingVO> resultList = htpWarehouseMappings.stream().map(HtpWarehouseMappingVO::new).collect(Collectors.toList());
            return R.ok(resultList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("解析返回异常：" + e.getMessage());
        }
    }

    /**
     * 查询仓库与仓库关联映射模块列表
     */
    @PreAuthorize("@ss.hasPermi('HtpWarehouseMapping:HtpWarehouseMapping:list')")
    @PostMapping("/list")
    @ApiOperation(value = "查询仓库与仓库关联映射模块列表", notes = "查询仓库与仓库关联映射模块列表")
    public TableDataInfo<HtpWarehouseMappingVO> list(@RequestBody HtpWarehouseMappingQueryDTO htpWarehouseMapping) {
        startPage(htpWarehouseMapping);
        List<HtpWarehouseMappingVO> list = htpWarehouseMappingService.selectHtpWarehouseMappingList(htpWarehouseMapping);
        return getDataTable(list);
    }

    /**
     * 获取仓库与仓库关联映射模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('HtpWarehouseMapping:HtpWarehouseMapping:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取仓库与仓库关联映射模块详细信息", notes = "获取仓库与仓库关联映射模块详细信息")
    public R<HtpWarehouseMappingVO> getInfo(@PathVariable("id") Integer id) {
        return R.ok(htpWarehouseMappingService.selectHtpWarehouseMappingById(id));
    }

    /**
     * 获取仓库与仓库关联映射模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('HtpWarehouseMapping:HtpWarehouseMapping:changeStatus')")
    @PutMapping(value = "changeStatus/{id}/{status}")
    @ApiOperation(value = "修改启用状态", notes = "修改启用状态")
    public R<HtpWarehouseMappingVO> changeStatus(@PathVariable("id") Integer id, @PathVariable("status") Integer status) {
        return R.ok(htpWarehouseMappingService.changeStatus(id, status));
    }

    /**
     * 新增仓库与仓库关联映射模块
     */
    @PreAuthorize("@ss.hasPermi('HtpWarehouseMapping:HtpWarehouseMapping:add')")
    @Log(title = "仓库与仓库关联映射模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增仓库与仓库关联映射模块", notes = "新增仓库与仓库关联映射模块")
    public R add(@Validated @RequestBody HtpWarehouseMappingDTO htpWarehouseMapping) {
        return toOk(htpWarehouseMappingService.insertHtpWarehouseMapping(htpWarehouseMapping));
    }

    /**
     * 修改仓库与仓库关联映射模块
     */
    @PreAuthorize("@ss.hasPermi('HtpWarehouseMapping:HtpWarehouseMapping:edit')")
    @Log(title = "仓库与仓库关联映射模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改仓库与仓库关联映射模块", notes = "修改仓库与仓库关联映射模块")
    public R edit(@Validated @RequestBody HtpWarehouseMappingDTO htpWarehouseMapping) {
        return toOk(htpWarehouseMappingService.updateHtpWarehouseMapping(htpWarehouseMapping));
    }

    /**
     * 删除仓库与仓库关联映射模块
     */
    @PreAuthorize("@ss.hasPermi('HtpWarehouseMapping:HtpWarehouseMapping:remove')")
    @Log(title = "仓库与仓库关联映射模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除仓库与仓库关联映射模块", notes = "删除仓库与仓库关联映射模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(htpWarehouseMappingService.deleteHtpWarehouseMappingByIds(ids));
    }

}
