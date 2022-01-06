package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.domain.BasSellerCertificate;
import com.szmsd.bas.dto.*;
import com.szmsd.bas.vo.BasSellerInfoVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
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
            public R<String> getSellerCode(@RequestBody BasSeller basSeller) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<String> getLoginSellerCode() {
                return R.convertResultJson(throwable);
            }

            @Override
            public TableDataInfo<BasSellerSysDto> list(BasSellerQueryDto basSeller) {
                throw new RuntimeException("获取用户列表失败" + throwable.getMessage());
            }

            @Override
            public R<String> getInspection(@RequestBody String sellerCode) {
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

            @Override
            public R<List<BasSellerCertificate>> listVAT(VatQueryDto vatQueryDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<BasSellerInfoVO> getInfo(String userName) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<BasSellerInfoVO> getInfoBySellerCode(String sellerCode) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
