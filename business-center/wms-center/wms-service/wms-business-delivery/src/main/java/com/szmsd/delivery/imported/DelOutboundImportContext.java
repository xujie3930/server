package com.szmsd.delivery.imported;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.dto.DelOutboundImportDto;
import com.szmsd.delivery.dto.DelOutboundPackageTransferImportDto;
import org.apache.commons.collections4.CollectionUtils;

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

        //导入模板是 SZ-深圳仓 这种格式，需要转换为SZ这种
        if (CollectionUtils.isNotEmpty(dataList)) {
            for (DelOutboundImportDto importDto : dataList) {
                if(StringUtils.contains(importDto.getWarehouseCode(), "-")){
                    importDto.setWarehouseCode(StringUtils.split(importDto.getWarehouseCode(), "-")[0]);
                }
            }
        }


    }
}
