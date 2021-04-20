package com.szmsd.http.service.http;

/**
 * @author zhangyuyuan
 * @date 2021-04-20 13:44
 */
@FunctionalInterface
public interface ReFunction<P1, P2, R> {

    R apply(P1 p1, P2 p2);
}
