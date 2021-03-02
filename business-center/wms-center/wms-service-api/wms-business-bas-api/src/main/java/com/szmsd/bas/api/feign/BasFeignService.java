package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.domain.*;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lufei
 * @version 1.0
 * @Date 2020-06-30 14:03
 * @Description
 */
@FeignClient(name = "business-bas")
@RequestMapping(produces = {"application/json;charset=UTF-8"})
public interface BasFeignService {

    /**
     * 查询客户员工资料
     *
     * @param basUser
     * @return
     */
    @PostMapping("/bas-user/lists")
    R<List<BasUser>> lists(@RequestBody BasUser basUser);

}
