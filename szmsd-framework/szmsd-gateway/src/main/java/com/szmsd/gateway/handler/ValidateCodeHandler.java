package com.szmsd.gateway.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import com.szmsd.common.core.domain.R;
import com.szmsd.gateway.service.ValidateCodeService;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * 验证码获取
 *
 * @author szmsd
 */
@Component
public class ValidateCodeHandler implements HandlerFunction<ServerResponse> {
    @Resource
    private ValidateCodeService validateCodeService;

    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        R ajax;
        try {
            ajax = validateCodeService.createCapcha();
        } catch (Exception e) {
            return Mono.error(e);
        }
        return ServerResponse.status(HttpStatus.OK).body(BodyInserters.fromValue(ajax));
    }
}