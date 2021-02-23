package com.szmsd.demo.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.demo.domain.BasCodeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author lufei
 * @version 1.0
 * @Date 2020-06-30 14:03
 * @Description
 */
@FeignClient(name = "business-bas")
@RequestMapping(produces = {"application/json;charset=UTF-8"})
public interface


BasFeignService {

    /**
     * 生成系统唯一订单号
     * @param basCodeDto
     * @return
     */
    @PostMapping(value = "/basCode/create")
    R<List<String>> create(@RequestBody BasCodeDto basCodeDto);
}
