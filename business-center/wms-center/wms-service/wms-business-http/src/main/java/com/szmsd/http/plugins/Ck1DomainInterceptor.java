package com.szmsd.http.plugins;

import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.common.core.domain.R;
import com.szmsd.http.util.DomainInterceptorUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component(value = "Ck1DomainInterceptor")
public class Ck1DomainInterceptor implements DomainInterceptor {
    private final Pattern p = Pattern.compile(DomainInterceptorUtil.REG_EX);
    private final Logger logger = LoggerFactory.getLogger(Ck1DomainInterceptor.class);

    @Autowired
    @SuppressWarnings({"all"})
    private BasSellerFeignService basSellerFeignService;

    @Override
    public boolean preHandle(String uri, Map<String, String> headers, String requestBody) {
        String sellerCode = headers.get(DomainInterceptorUtil.KEYWORD);
        if (StringUtils.isEmpty(sellerCode)) {
            Matcher matcher = p.matcher(requestBody);
            while (matcher.find()) {
                String group = matcher.group();
                String wrapperSellerCode = group.substring(group.indexOf(":") + 1);
                sellerCode = wrapperSellerCode.substring(1, wrapperSellerCode.length() - 1);
            }
        }
        boolean returnValue = true;
        try {
            // 根据客户编码查询状态信息
            R<Boolean> booleanR = this.basSellerFeignService.queryCkPushFlag(sellerCode);
            if (null != booleanR) {
                Boolean aBoolean = booleanR.getData();
                if (null != aBoolean) {
                    returnValue = aBoolean;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        headers.put("_return_value", String.valueOf(returnValue));
        // 默认返回true
        return returnValue;
    }
}
