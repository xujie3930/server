package com.szmsd.system.api.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.system.api.domain.dto.SysLangDTO;
import com.szmsd.system.api.feign.I18nFeignService;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 系统服务多语言降级处理
 *
 * @author szmsd
 */
@Component
public class I18nFallbackFactory  implements FallbackFactory<I18nFeignService> {

    private static final Logger log = LoggerFactory.getLogger(I18nFallbackFactory.class);

    @Override
    public I18nFeignService create(Throwable throwable) {
        log.error("系统服务多语言调用失败:{}", throwable.getMessage());
        return new I18nFeignService()
        {
            @Override
            public R<List<SysLangDTO>> queryAllLang(SysLangDTO sysLangDTO) {
                return null;
            }

            @Override
            public R<String> getTableUpdateTime(String tableName) {
                return null;
            }
        };
    }
}
