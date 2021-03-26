package com.szmsd.http.service;

import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.dto.RemoteAreaTemplateIdCriteria;
import com.szmsd.http.dto.RemoteAreaTemplateSearchCriteria;
import com.szmsd.http.vo.ImportResult;
import com.szmsd.http.vo.RemoteAreaTemplate;
import org.springframework.web.multipart.MultipartFile;

public interface IRemoteAreaTemplateService {

    PageVO<RemoteAreaTemplate> pageResult(RemoteAreaTemplateSearchCriteria remoteAreaTemplateSearchCriteria);

    ImportResult importFile(MultipartFile file);

    FileStream exportFile(RemoteAreaTemplateIdCriteria remoteAreaTemplateIdCriteria);
}
