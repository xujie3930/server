package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasWarehouseFeignService;
import com.szmsd.bas.api.feign.EmailFeingService;
import com.szmsd.bas.dto.AddWarehouseRequest;
import com.szmsd.bas.dto.EmailDto;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailFeingFallback implements FallbackFactory<EmailFeingService> {
    @Override
    public EmailFeingService create(Throwable throwable) {
        return new EmailFeingService(){
            @Override
            public R sendEmail(EmailDto emailDto) {
                log.info("邮件发送失败: {}", throwable.getMessage());
                return R.convertResultJson(throwable);
            }

            @Override
            public R sendEmailError(EmailDto emailDto) {
                log.info("邮件发送失败: {}", throwable.getMessage());
                return R.convertResultJson(throwable);
            }
        };
    }
}
