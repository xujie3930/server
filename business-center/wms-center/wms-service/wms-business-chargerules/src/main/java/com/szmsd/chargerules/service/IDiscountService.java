package com.szmsd.chargerules.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.http.dto.discount.*;

public interface IDiscountService {

    R<DiscountMainDto> detailResult(String id);

    TableDataInfo<DiscountMainDto> page(DiscountPageRequest pageDTO);

    R detailImport(UpdateDiscountDetailDto dto);

    R customUpdate(UpdateDiscountCustomDto dto);

    R create(MergeDiscountDto dto);

    R update(MergeDiscountDto dto);

}
