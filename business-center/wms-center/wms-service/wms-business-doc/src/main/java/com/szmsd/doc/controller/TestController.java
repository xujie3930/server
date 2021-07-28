package com.szmsd.doc.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangyuyuan
 * @date 2021-07-28 9:28
 */
@RestController
public class TestController {

    @GetMapping("/echo")
    @PreAuthorize("hasAuthority('read')")
    public String echo() {
        return "echo ... ";
    }
}
