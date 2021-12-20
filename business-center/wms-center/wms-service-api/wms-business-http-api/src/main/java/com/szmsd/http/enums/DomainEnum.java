package com.szmsd.http.enums;

import com.szmsd.http.util.DomainUtil;

public enum DomainEnum {
    /**
     * ck1 open api
     */
    Ck1OpenAPIDomain,

    ;

    /**
     * 包装
     *
     * @param api api
     * @return ${xxx}/api
     */
    public String wrapper(String api) {
        return DomainUtil.wrapper(this, api);
    }
}
