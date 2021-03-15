package com.szmsd.chargerules.service;


import com.msd.chargerules.domain.Operation;
import com.msd.chargerules.dto.OperationDTO;

import java.util.List;

public interface IOperationService {

    int save(OperationDTO dto);

    int update(Operation dto);

    List<Operation> listPage(OperationDTO dto);

}
