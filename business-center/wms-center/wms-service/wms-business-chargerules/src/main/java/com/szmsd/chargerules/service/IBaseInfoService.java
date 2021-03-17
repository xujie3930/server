package com.szmsd.chargerules.service;


import com.msd.chargerules.domain.BasSpecialOperation;
import com.msd.chargerules.dto.BasSpecialOperationDTO;
import com.szmsd.common.core.domain.R;
import com.szmsd.open.vo.ResponseVO;

import java.util.List;

public interface IBaseInfoService {

    ResponseVO add(BasSpecialOperationDTO basSpecialOperationDTO);

    List<BasSpecialOperation> list(BasSpecialOperationDTO basSpecialOperationDTO);

    R update(BasSpecialOperation basSpecialOperation);
}
