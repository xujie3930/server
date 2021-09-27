package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.domain.BasRegion;
import com.szmsd.bas.api.domain.dto.BasRegionSelectListQueryDto;
import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 19:26
 */
@Component
public class BasRegionFeignServiceFallbackFactory implements FallbackFactory<BasRegionFeignService> {

    @Override
    public BasRegionFeignService create(Throwable throwable) {
        return new BasRegionFeignService() {

            @Override
            public TableDataInfo<BasRegion> list(BasRegion basRegion) {
                return null;
            }

            @Override
            public R<List<BasRegionSelectListVO>> countryList(BasRegionSelectListQueryDto queryDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<BasRegionSelectListVO> queryByCountryCode(String addressCode) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<BasRegionSelectListVO> queryByCountryName(String addressName) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
