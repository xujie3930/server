package com.szmsd.common.core.interceptor;

import com.szmsd.common.core.config.TokenConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangyuyuan
 * @date 2021-05-10 13:38
 */
public class TokenVerificationAdapter implements TokenVerification {

    private final Verification verification;

    public TokenVerificationAdapter(RedisTemplate<Object, Object> redisTemplate, TokenConfig tokenConfig) {
        this.verification = new Verification.TokenVerification(redisTemplate, tokenConfig);
    }

    @Override
    public boolean handler(HttpServletRequest request, HttpServletResponse response) throws TokenException {
        String tokenVerification = request.getHeader(TokenConstant.PREFIX + TokenConstant.SEPARATOR + TokenConstant.TOKEN_VERIFICATION);
        if (StringUtils.isEmpty(tokenVerification)) {
            // 没有tokenVerification不处理
            this.setTRV(response, "No-Token-Verification");
            return true;
        }
        VerificationEnum verificationEnum = VerificationEnum.valueOf2(tokenVerification);
        if (null == verificationEnum) {
            // 类型不匹配
            this.setTRV(response, "No-Enum");
            return true;
        }
        String token = request.getHeader(TokenConstant.PREFIX + TokenConstant.SEPARATOR + TokenConstant.TOKEN);
        if (StringUtils.isEmpty(token)) {
            // 没有token
            this.setTRV(response, "No-Token");
            return true;
        }
        this.setTRV(response, "By-" + verificationEnum.name());
        this.verification.verify(request, verificationEnum, token);
        return true;
    }

    /**
     * 设置T-R-V header
     *
     * @param response response
     * @param value    value
     */
    private void setTRV(HttpServletResponse response, String value) {
        ResponseUtil.setHeader(response, TokenConstant.PREFIX + TokenConstant.SEPARATOR + TokenConstant.R_V, value);
    }
}
