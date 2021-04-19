package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpRemoteAreaTemplateFeignFallback;
import com.szmsd.http.dto.RemoteAreaTemplateIdCriteria;
import com.szmsd.http.dto.RemoteAreaTemplateSearchCriteria;
import com.szmsd.http.vo.ImportResult;
import com.szmsd.http.vo.RemoteAreaTemplate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(contextId = "FeignClient.HtpRemoteAreaTemplateFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpRemoteAreaTemplateFeignFallback.class)
public interface HtpRemoteAreaTemplateFeignService {

    @PostMapping("/api/remote/area/http/pageResult")
    R<PageVO<RemoteAreaTemplate>> pageResult(@RequestBody RemoteAreaTemplateSearchCriteria remoteAreaTemplateSearchCriteria);

    @PostMapping(value = "/api/remote/area/http/importFile", headers = "content-type=multipart/form-data")
    R<ImportResult> importFile(@RequestPart("file") MultipartFile file);

    @PostMapping("/api/remote/area/http/exportFile")
    R<FileStream> exportFile(@RequestBody RemoteAreaTemplateIdCriteria remoteAreaTemplateIdCriteria);

}
