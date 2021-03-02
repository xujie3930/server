package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.domain.*;
import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户服务降级处理
 *
 * @author szmsd
 */
@Component
public class BasFeignServiceFallbackFactory implements FallbackFactory<BasFeignService> {
    private static final Logger log = LoggerFactory.getLogger(BasFeignServiceFallbackFactory.class);

    @Override
    public BasFeignService create(Throwable throwable) {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new BasFeignService() {


            @Override
            public R<List<String>> create(BasCodeDto basCodeDto) {
                return null;
            }

            @Override
            public R<BigDecimal> cus(String costCategory, String productTypeCode, String cusCode, String heft, String destinationCode) {
                return null;
            }

            @Override
            public R<List<BasMain>> lists(BasMain basMain) {
                return null;
            }

            @Override
            public R list(String code, String name) {
                return null;
            }

            @Override
            public R<List<BasCustomer>> lists(BasCustomer basCustomer) {
                return null;
            }

            @Override
            public R<List<BasUser>> lists(BasUser basUser) {
                return null;
            }

            @Override
            public R<List<BasProductType>> lists(BasProductType basProductType) {
                return null;
            }

            @Override
            public R<List<BasDestination>> getdstination(BasDestination basDestination) {
                return null;
            }

            @Override
            public R<List<BasKeyword>> lists(BasKeyword basKeyword) {
                return null;
            }

            @Override
            public R<List<BasSub>> getsub(BasSub basSub) {
                return null;
            }

            @Override
            public R remove(List<String> ids) {
                return null;
            }

            @Override
            public R removes(String businesSiteCode) {
                return null;
            }

            @Override
            public R<List<BasRoute>> lists(BasRoute basRoute) {
                return null;
            }

            @Override
            public R<List<BasAppMes>> getPushAppMsgList() {
                return null;
            }

            @Override
            public R updateBasAppMesList(List<BasAppMes> basAppMesList) {
                return null;
            }

            @Override
            public R<List<BasWeightSectionVo>> weightList(String userCode) {
                return null;
            }

            @Override
            public R<Integer> weightSave(BasWeightSectionDto dto) {
                return null;
            }

            @Override
            public R<BasEmployees> getEmpByCode(BasEmployees basEmployees) {
                return null;
            }

            @Override
            public R<BasApiCountry> getCountryByName(String name) {
                return null;
            }

            @Override
            public R<BasApiCity> getBasApiCity(String provinceName, String cityName, String townName) {
                return null;
            }

            @Override
            public R<List<BasEmployees>> empList(BasEmployees basEmployees) {
                return null;
            }
        };
    }
}
