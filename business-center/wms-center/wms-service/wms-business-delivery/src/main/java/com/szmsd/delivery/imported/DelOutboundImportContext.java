package com.szmsd.delivery.imported;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.delivery.dto.DelOutboundImportDto;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 20:07
 */
public class DelOutboundImportContext extends DelOutboundCacheImportContext {

    public DelOutboundImportContext(List<DelOutboundImportDto> dataList,
                                    List<BasSubWrapperVO> orderTypeList,
                                    List<BasRegionSelectListVO> countryList,
                                    List<BasSubWrapperVO> deliveryMethodList) {
        super(dataList, orderTypeList, countryList, deliveryMethodList);
    }
}
