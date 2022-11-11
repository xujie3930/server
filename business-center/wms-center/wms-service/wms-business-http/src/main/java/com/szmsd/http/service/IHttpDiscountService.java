package com.szmsd.http.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.dto.OperationRecordDto;
import com.szmsd.http.dto.discount.*;
import com.szmsd.http.dto.discount.DiscountPage;

public interface IHttpDiscountService {

        R<DiscountMainDto> detailResult(String id);

        R<PageVO<DiscountDetailDto>> detailResultPage(DiscountPage discountPage);

        R<PageVO> page(DiscountPageRequest pageDTO);

        R detailImport(UpdateDiscountDetailDto dto);

        R customUpdate(UpdateDiscountCustomDto dto);

        R create(MergeDiscountDto dto);

        R update(MergeDiscountDto dto);


    R<OperationRecordDto> operationRecord(String id);

    R insertBasCodExternal();
}
