package com.szmsd.http.servlet.matcher;

/**
 * @author zhangyuyuan
 * @date 2021-05-07 9:39
 */
public class PathRequestForwardMatcher implements RequestForwardMatcher {

    private final PathContext pathContext;

    public PathRequestForwardMatcher(PathContext pathContext) {
        this.pathContext = pathContext;
    }

    @Override
    public boolean match(String path) {
        return this.pathContext.has(path);
    }
}
