package com.szmsd.bas.driver;

import com.szmsd.bas.service.BasCommonService;
import com.szmsd.common.core.enums.CodeToNameEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author liyingfeng
 * @date 2020/11/19 20:18
 */
@Configuration
@Slf4j
public class MyPostConstruct {

    @Resource
    private BasCommonService commonService;

    @PostConstruct
    public void load() {
        for (CodeToNameEnum e : CodeToNameEnum.values()) {
            commonService.updateRedis(e);
        }
    }

}
