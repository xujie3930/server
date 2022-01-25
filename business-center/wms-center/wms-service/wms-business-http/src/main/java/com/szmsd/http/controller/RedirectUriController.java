package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.utils.RedirectUriUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Api(tags = {"HTTP回调接收接口"})
@ApiSort(10000)
@RestController
@RequestMapping("/api/redirect/uri")
public class RedirectUriController {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @GetMapping
    @ApiOperation(value = "HTTP回调接收接口 - #1", position = 100)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "state", value = "回调状态"),
            @ApiImplicitParam(name = "code", value = "授权校验码", required = true)
    })
    public R<String> redirectUri(@RequestParam(value = "state") String state,
                                 @RequestParam(value = "code") String code) {
        String wrapRedirectUriKey = RedirectUriUtil.wrapRedirectUriKey(state);
        // 设置到缓存中，有效期1小时
        this.redisTemplate.opsForValue().set(wrapRedirectUriKey, code, 1, TimeUnit.HOURS);
        return R.ok(state);
    }
}
