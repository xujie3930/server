package com.szmsd.http.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.dto.grade.*;

public interface IHttpGradeService {

        R<GradeMainDto> detailResult(String id);

        R<PageVO<GradeMainDto>> page(GradePageRequest pageDTO);

        R detailImport(UpdateGradeDetailDto dto);

        R customUpdate(UpdateGradeCustomDto dto);

        R create(MergeGradeDto dto);

        R update(MergeGradeDto dto);



}
