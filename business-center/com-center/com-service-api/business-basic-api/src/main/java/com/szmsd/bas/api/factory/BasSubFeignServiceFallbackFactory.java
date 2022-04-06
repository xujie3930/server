package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.bas.api.feign.BasSubFeignService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-03-26 10:45
 */
@Component
public class BasSubFeignServiceFallbackFactory implements FallbackFactory<BasSubFeignService> {

    @Override
    public BasSubFeignService create(Throwable throwable) {
        return new BasSubFeignService() {
            @Override
            public List<BasSub> listApi(String mainCode, String subValue) {
                return null;
            }

            @Override
            public R<List<BasSub>> listByMain(String mainCode, String mainName) {
                return  R.convertResultJson(throwable);
            }

            @Override
            public R<Map<String, List<BasSubWrapperVO>>> getSub(String code) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Map<String,String>> getSubList(@RequestParam("code") String code){
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Map<String, String>> getSubListByLang(String code, String lang) {
                return R.convertResultJson(throwable);
            }

//            @Override
//            public R<?> list(String code, String name) {
//                return R.convertResultJson(throwable);
//            }

        };
    }
}
