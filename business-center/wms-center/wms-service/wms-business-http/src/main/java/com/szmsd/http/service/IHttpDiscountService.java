package com.szmsd.http.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.dto.discount.*;

public interface IHttpDiscountService {

        R<DiscountMainDto> detailResult(String id);

        R<PageVO> page(DiscountPageRequest pageDTO);

        R detailImport(UpdateDiscountDetailDto dto);

        R customUpdate(UpdateDiscountCustomDto dto);

        R create(MergeDiscountDto dto);

        R update(MergeDiscountDto dto);



}
