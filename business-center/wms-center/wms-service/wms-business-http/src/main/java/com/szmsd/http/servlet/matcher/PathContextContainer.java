package com.szmsd.http.servlet.matcher;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhangyuyuan
 * @date 2021-05-07 9:44
 */
@Component
public class PathContextContainer implements PathContext {

    private final Set<String> paths;

    public PathContextContainer() {
        this.paths = new HashSet<>();
    }

    @Override
    public void add(String path) {
        this.paths.add(path);
    }

    @Override
    public void add(Collection<String> paths) {
        this.paths.addAll(paths);
    }

    @Override
    public boolean has(String path) {
        return this.paths.contains(path);
    }
}
