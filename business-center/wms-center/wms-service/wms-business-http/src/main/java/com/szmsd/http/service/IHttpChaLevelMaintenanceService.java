package com.szmsd.http.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenancePageRequest;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenanceDto;
import com.szmsd.http.vo.ResponseVO;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

public interface IHttpChaLevelMaintenanceService {

    R<PageVO> page(ChaLevelMaintenancePageRequest pageDTO);

    R<ChaLevelMaintenanceDto> get(String id);

    R create(ChaLevelMaintenanceDto chaLevelMaintenanceCreate);

    R update(ChaLevelMaintenanceDto chaLevelMaintenanceUpdate);

    R delete(String id);


    R<List<ChaLevelMaintenanceDto>> list(ChaLevelMaintenanceDto pageDTO);
}
