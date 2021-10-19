package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.domain.BasRegion;
import com.szmsd.bas.api.domain.dto.BasRegionQueryDTO;
import com.szmsd.bas.api.domain.dto.BasRegionSelectListQueryDto;
import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.factory.BasRegionFeignServiceFallbackFactory;
import com.szmsd.common.core.constant.ServiceNameConstants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 19:23
 */
@FeignClient(contextId = "BasRegionFeignService", value = ServiceNameConstants.BUSINESS_BAS, fallbackFactory = BasRegionFeignServiceFallbackFactory.class)
public interface BasRegionFeignService {

    @RequestMapping(value = "/bas-region/list", method = RequestMethod.GET)
    TableDataInfo<BasRegion> list(BasRegionQueryDTO basRegion);

    @RequestMapping(value = "/bas-region/countryList", method = RequestMethod.GET)
    R<List<BasRegionSelectListVO>> countryList(BasRegionSelectListQueryDto queryDto);

    @RequestMapping("/bas-region/queryByCountryCode")
    R<BasRegionSelectListVO> queryByCountryCode(@RequestParam("addressCode") String addressCode);

    @RequestMapping("/bas-region/queryByCountryName")
    R<BasRegionSelectListVO> queryByCountryName(@RequestParam("addressName") String addressName);
}
