package com.szmsd.http.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenanceDto;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenancePageRequest;
import com.szmsd.http.dto.custom.DiscountMainDto;

public interface IHttpDiscountService {

        R<DiscountMainDto> detailResult(String id);


}
