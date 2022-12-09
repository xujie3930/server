package com.szmsd.chargerules.api.feign.factory;

import com.szmsd.chargerules.api.feign.ChargeFeignService;
import com.szmsd.chargerules.domain.BasProductService;
import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.dto.BasProductServiceDao;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChargeFeignFallback implements FallbackFactory<ChargeFeignService> {

    @Override
    public ChargeFeignService create(Throwable throwable) {
        return new ChargeFeignService() {
            @Override
            public R<TableDataInfo<QueryChargeVO>> selectPage(QueryChargeDto queryDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R add(ChargeLog chargeLog) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<BasProductService>> selectBasProductService(List<String> list) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<BasProductService> selectBasProductServiceeOne(BasProductServiceDao basProductServiceDao) {
                return  R.convertResultJson(throwable);
            }

        };
    }
}
