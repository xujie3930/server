package com.szmsd.doc.api;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.SpringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CountryCache {

    private CountryCache() {
    }

    private static Map<String, String> countryMap = new ConcurrentHashMap<>(32);
    private static BasRegionFeignService basRegionFeignService;

    public static synchronized String getCountry(String countryCode) {
        if (countryMap.containsKey(countryCode)) {
            return countryMap.get(countryCode);
        }
        String country = getRegionCountry(countryCode);
        if (null != country) {
            countryMap.put(countryCode, country);
        }
        return country;
    }

    private static String getRegionCountry(String countryCode) {
        if (null == basRegionFeignService) {
            basRegionFeignService = SpringUtils.getBean(BasRegionFeignService.class);
        }
        R<BasRegionSelectListVO> basRegionSelectListVOR = basRegionFeignService.queryByCountryCode(countryCode);
        BasRegionSelectListVO vo = R.getDataAndException(basRegionSelectListVOR);
        if (null != vo) {
            return vo.getName();
        }
        return null;
    }
}
