package com.szmsd.http.service.impl;


import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.grade.*;
import com.szmsd.http.service.IHttpGradeService;
import com.szmsd.http.service.http.SaaSPricedRequest;
import com.szmsd.http.util.HttpResponseVOUtils;
import org.springframework.stereotype.Service;

@Service
public class GradeServiceImpl extends SaaSPricedRequest implements IHttpGradeService {

    public GradeServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }


    @Override
    public R<GradeMainDto> detailResult(String id) {
        return HttpResponseVOUtils.transformation(httpGetBody("", "grade.detailResult", null, id), GradeMainDto.class);
    }

    @Override
    public R<PageVO> page(GradePageRequest pageDTO) {
        return HttpResponseVOUtils.transformationPage(httpPostBody("", "grade.page", pageDTO), GradeMainDto.class);
    }

    @Override
    public R detailImport(UpdateGradeDetailDto dto) {
        return HttpResponseVOUtils.transformation(httpPutBody("", "grade.detailImport", dto, dto.getTemplateId()));
    }

    @Override
    public R customUpdate(UpdateGradeCustomDto dto) {
        return HttpResponseVOUtils.transformation(httpPutBody("", "grade.customUpdate", dto, dto.getTemplateId()));
    }

    @Override
    public R create(MergeGradeDto dto) {
        return HttpResponseVOUtils.transformation(httpPostBody("", "grade.create", dto));
    }

    @Override
    public R update(MergeGradeDto dto) {
        return HttpResponseVOUtils.transformation(httpPutBody("", "grade.update", dto, dto.getId()));
    }
}
