package com.szmsd.chargerules.service;


import com.szmsd.chargerules.domain.BasSpecialOperation;
import com.szmsd.chargerules.dto.BasSpecialOperationRequestDTO;
import com.szmsd.chargerules.vo.BasSpecialOperationVo;
import com.szmsd.common.core.domain.R;

import java.util.List;

public interface IBaseInfoService {

    void add(BasSpecialOperationRequestDTO basSpecialOperationRequestDTO);

    List<BasSpecialOperationVo> list(BasSpecialOperationRequestDTO basSpecialOperationRequestDTO);

    R update(BasSpecialOperation basSpecialOperation);

    BasSpecialOperationVo details(int id);

    R updateApprova(List<BasSpecialOperation> basSpecialOperations);
}
