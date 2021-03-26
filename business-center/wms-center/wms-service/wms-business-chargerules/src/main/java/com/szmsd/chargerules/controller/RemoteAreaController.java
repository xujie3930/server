package com.szmsd.chargerules.controller;

import com.szmsd.chargerules.dto.RemoteAreaQueryDTO;
import com.szmsd.chargerules.service.IRemoteAreaService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.http.dto.RemoteAreaTemplateIdCriteria;
import com.szmsd.http.vo.ImportResult;
import com.szmsd.http.vo.RemoteAreaTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"RemoteArea"})
@RestController
@RequestMapping("/remote/area")
public class RemoteAreaController extends BaseController {

    @Resource
    private IRemoteAreaService iRemoteAreaService;

    @PostMapping("/page")
    @ApiOperation(value = "分页查询地址库模板列表，返回指定页面的数据，以及统计总记录数")
    public TableDataInfo<RemoteAreaTemplate> page(@RequestBody RemoteAreaQueryDTO remoteAreaQueryDTO) {
        TableDataInfo<RemoteAreaTemplate> remoteAreaTemplateList = iRemoteAreaService.page(remoteAreaQueryDTO);
        return remoteAreaTemplateList;
    }

    @PostMapping(value = "/importFile", headers = "content-type=multipart/form-data")
    @ApiOperation(value = "导入地址库模板")
    public R<ImportResult> importFile(@RequestBody MultipartFile file) {
        ImportResult importFile = iRemoteAreaService.importFile(file);
        return R.ok(importFile);
    }

    @PostMapping("/exportFile")
    @ApiOperation(value = "导出地址库模板信息")
    public void exportFile(HttpServletResponse response, @RequestBody RemoteAreaTemplateIdCriteria remoteAreaTemplateIdCriteria) {
        FileStream fileStream = iRemoteAreaService.exportFile(remoteAreaTemplateIdCriteria);
        super.fileStreamWrite(response, fileStream);
    }

}
