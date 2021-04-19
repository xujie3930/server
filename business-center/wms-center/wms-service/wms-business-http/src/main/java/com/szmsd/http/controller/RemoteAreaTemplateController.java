package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.dto.RemoteAreaTemplateIdCriteria;
import com.szmsd.http.dto.RemoteAreaTemplateSearchCriteria;
import com.szmsd.http.service.IRemoteAreaTemplateService;
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

@Api(tags = {"RemoteAreaTemplate"})
@RestController
@RequestMapping("/api/remote/area/http")
public class RemoteAreaTemplateController extends BaseController {

    @Resource
    private IRemoteAreaTemplateService iRemoteAreaTemplateService;

    @PostMapping("/pageResult")
    @ApiOperation(value = "分页查询地址库模板列表，返回指定页面的数据，以及统计总记录数")
    public R<PageVO<RemoteAreaTemplate>> pageResult(@RequestBody RemoteAreaTemplateSearchCriteria remoteAreaTemplateSearchCriteria) {
        PageVO<RemoteAreaTemplate> pageResult = iRemoteAreaTemplateService.pageResult(remoteAreaTemplateSearchCriteria);
        return pageResult == null ? R.ok(PageVO.empty()) : R.ok(pageResult);
    }

    @PostMapping(value = "/importFile", headers = "content-type=multipart/form-data")
    @ApiOperation(value = "导入地址库模板")
    public R<ImportResult> importFile(@RequestBody MultipartFile file) {
        ImportResult importFile = iRemoteAreaTemplateService.importFile(file);
        return R.ok(importFile);
    }

    @PostMapping("/exportFile")
    @ApiOperation(value = "导出地址库模板信息")
    public R<FileStream> exportFile(@RequestBody RemoteAreaTemplateIdCriteria remoteAreaTemplateIdCriteria) {
        FileStream fileStream = iRemoteAreaTemplateService.exportFile(remoteAreaTemplateIdCriteria);
        return R.ok(fileStream);
    }

}
