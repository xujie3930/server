package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.HtpRemoteAreaTemplateFeignService;
import com.szmsd.http.dto.RemoteAreaTemplateIdCriteria;
import com.szmsd.http.dto.RemoteAreaTemplateSearchCriteria;
import com.szmsd.http.vo.ImportResult;
import com.szmsd.http.vo.RemoteAreaTemplate;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class HtpRemoteAreaTemplateFeignFallback implements FallbackFactory<HtpRemoteAreaTemplateFeignService> {
    @Override
    public HtpRemoteAreaTemplateFeignService create(Throwable throwable) {
        log.error("{}服务调用失败：{}", BusinessHttpInterface.SERVICE_NAME, throwable.getMessage());
        return new HtpRemoteAreaTemplateFeignService() {
            @Override
            public PageVO<RemoteAreaTemplate> pageResult(RemoteAreaTemplateSearchCriteria remoteAreaTemplateSearchCriteria) {
                return PageVO.empty();
            }

            @Override
            public R<ImportResult> importFile(MultipartFile file) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<FileStream> exportFile(RemoteAreaTemplateIdCriteria remoteAreaTemplateIdCriteria) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
