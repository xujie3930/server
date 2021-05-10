package com.szmsd.common.core.interceptor;

import cn.hutool.crypto.SecureUtil;

/**
 * @author zhangyuyuan
 * @date 2021-05-10 14:26
 */
public interface OnlyKey {

    /**
     * 处理唯一值
     *
     * @param method           method
     * @param url              url
     * @param verificationEnum verificationEnum
     * @param token            token
     * @return String
     */
    String handler(String method, String url, VerificationEnum verificationEnum, String token);

    /**
     * md5处理唯一值
     */
    class Md5OnlyKey implements OnlyKey {

        private final String applicationName;

        public Md5OnlyKey(String applicationName) {
            this.applicationName = applicationName;
        }

        @Override
        public String handler(String method, String url, VerificationEnum verificationEnum, String token) {
            return this.applicationName + ":Token:" + verificationEnum.name() + ":" + SecureUtil.md5(method + url) + ":" + token;
        }
    }
}
