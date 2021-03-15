package com.szmsd.chargerules.service;


import com.msd.chargerules.domain.WarehouseOperation;
import com.msd.chargerules.dto.WarehouseOperationDTO;

import java.util.List;

public interface IWarehouseOperationService {

    int save(WarehouseOperationDTO dto);

    int update(WarehouseOperation dto);

    List<WarehouseOperation> listPage(WarehouseOperationDTO dto);

}
