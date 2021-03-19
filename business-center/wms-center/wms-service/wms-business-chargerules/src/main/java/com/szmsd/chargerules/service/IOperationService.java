package com.szmsd.chargerules.service;


import com.szmsd.chargerules.domain.Operation;
import com.szmsd.chargerules.dto.OperationDTO;

import java.util.List;

public interface IOperationService {

    int save(OperationDTO dto);

    int update(Operation dto);

    List<Operation> listPage(OperationDTO dto);

}
