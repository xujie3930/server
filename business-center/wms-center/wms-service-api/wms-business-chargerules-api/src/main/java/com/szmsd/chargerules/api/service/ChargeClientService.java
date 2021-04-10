package com.szmsd.chargerules.api.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;

public interface ChargeClientService {

    R<TableDataInfo<QueryChargeVO>> selectPage(QueryChargeDto queryDto);

}
