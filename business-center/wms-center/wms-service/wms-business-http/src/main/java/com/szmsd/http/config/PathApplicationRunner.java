package com.szmsd.http.config;

import com.szmsd.http.servlet.matcher.PathContext;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author zhangyuyuan
 * @date 2021-05-07 10:02
 */
@Component
public class PathApplicationRunner implements ApplicationRunner {

    private final PathConfig pathConfig;
    private final PathContext pathContext;

    public PathApplicationRunner(PathConfig pathConfig, PathContext pathContext) {
        this.pathConfig = pathConfig;
        this.pathContext = pathContext;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Set<String> paths = this.pathConfig.getPaths();
        if (CollectionUtils.isNotEmpty(paths)) {
            this.pathContext.add(paths);
        }
    }
}
