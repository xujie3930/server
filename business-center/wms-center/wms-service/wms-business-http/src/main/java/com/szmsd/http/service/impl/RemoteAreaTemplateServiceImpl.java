package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.RemoteAreaTemplateIdCriteria;
import com.szmsd.http.dto.RemoteAreaTemplateSearchCriteria;
import com.szmsd.http.service.IRemoteAreaTemplateService;
import com.szmsd.http.service.http.SaaSProductRemoteAreaRequest;
import com.szmsd.http.vo.ImportResult;
import com.szmsd.http.vo.RemoteAreaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RemoteAreaTemplateServiceImpl extends SaaSProductRemoteAreaRequest implements IRemoteAreaTemplateService {

    public RemoteAreaTemplateServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public PageVO<RemoteAreaTemplate> pageResult(RemoteAreaTemplateSearchCriteria remoteAreaTemplateSearchCriteria) {
        return JSON.parseObject(httpPost("", "remoteAreaTemplate.pageResult", remoteAreaTemplateSearchCriteria), new TypeReference<PageVO<RemoteAreaTemplate>>() {
        });
    }

    @Override
    public ImportResult importFile(MultipartFile file) {
        return JSON.parseObject(httpPostMuFile("", "remoteAreaTemplate.importFile", null, file), ImportResult.class);
    }

    @Override
    public FileStream exportFile(RemoteAreaTemplateIdCriteria remoteAreaTemplateIdCriteria) {
        return httpPostFile("", "remoteAreaTemplate.exportFile", remoteAreaTemplateIdCriteria);
    }
}
