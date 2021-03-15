package com.szmsd.chargerules.service;


import com.msd.chargerules.domain.SpecialOperation;
import com.msd.chargerules.dto.SpecialOperationDTO;

import java.util.List;

public interface ISpecialOperationService {

    int save(SpecialOperationDTO dto);

    int update(SpecialOperation dto);

    List<SpecialOperation> listPage(SpecialOperationDTO dto);

}
