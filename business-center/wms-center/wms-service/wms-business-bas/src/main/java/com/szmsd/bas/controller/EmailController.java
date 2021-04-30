package com.szmsd.bas.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.enums.EmailEnum;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.bas.util.EmailUtil;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.redis.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Api(tags = {"邮件模块"})
@RestController
@RequestMapping("/bas/email")
public class EmailController extends BaseController {

    @Resource
    private EmailUtil emailUtil;

    @Resource
    private RedisService redisService;

    @Autowired
    private IBasSellerService basSellerService;

    @Resource
    private Executor asyncTaskExecutor;

    @PreAuthorize("@ss.hasPermi('bas:email:sendvercode')")
    @GetMapping("/sendVerCode/{email}")
    @ApiOperation(value = "发送验证码", notes = "发送验证码")
    public R sendVerCode(@PathVariable("email") String email) {
        boolean isEmail = EmailUtil.isEmail(email);
        AssertUtil.isTrue(isEmail, "请填写正确的邮箱格式");
        QueryWrapper<BasSeller> queryWrapperEmail = new QueryWrapper<>();
        queryWrapperEmail.eq("init_email",email);
        int count = basSellerService.count(queryWrapperEmail);
        if(count!=0){
           throw new BaseException("邮箱重复，请更换邮箱");
        }
        EmailEnum varCode = EmailEnum.VAR_CODE;
        String key = varCode.name().concat("-").concat(email);
        String cacheObject = redisService.getCacheObject(key);
        AssertUtil.isTrue(StringUtils.isEmpty(cacheObject), "验证码已发送，不要重复频繁获取");
        // 随机验证码
        String code = RandomStringUtils.random(4, false, true);
        // 设置redis失效时间 30分钟
        redisService.setCacheObject(key, code, varCode.getTimeout(), varCode.getTimeUnit());
        CompletableFuture.runAsync(() -> {
            // 发送
            try {
                emailUtil.sendHtmlMail(email, varCode.getTitle(), varCode.get(code));
            } catch (Exception e) {
                redisService.deleteObject(key);
                log.error("邮件发送失败，请稍后重试, {}, {}", email, e.getMessage());
            }
        }, asyncTaskExecutor);
        return R.ok();
    }

}
