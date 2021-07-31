package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.dto.BasSellerEmailDto;
import com.szmsd.bas.dto.ServiceConditionDto;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
public class BasSellerFeignFallback implements FallbackFactory<BasSellerFeignService> {
    @Override
    public BasSellerFeignService create(Throwable throwable) {
        return new BasSellerFeignService() {
            @Override
            public  R<String> getSellerCode(@RequestBody BasSeller basSeller){
                return R.convertResultJson(throwable);
            }
            @Override
            public R<String> getLoginSellerCode(){
                return R.convertResultJson(throwable);
            }
            @Override
            public R<String> getInspection(@RequestBody String sellerCode){
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<String>> queryByServiceCondition(ServiceConditionDto conditionDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<BasSellerEmailDto>> queryAllSellerCodeAndEmail() {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<String> getRealState(String sellerCode) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
