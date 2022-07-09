package com.szmsd.chargerules.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.http.dto.OperationRecordDto;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenanceDto;
import com.szmsd.http.dto.custom.*;
import com.szmsd.http.dto.grade.*;

public interface IGradeService {
    R<OperationRecordDto> operationRecord(String id);

    R<GradeMainDto> detailResult(String id);

    TableDataInfo<GradeMainDto> page(GradePageRequest pageDTO);

    R detailImport(UpdateGradeDetailDto dto);

    R customUpdate(UpdateGradeCustomDto dto);

    R create(MergeGradeDto dto);

    R update(MergeGradeDto dto);

}
