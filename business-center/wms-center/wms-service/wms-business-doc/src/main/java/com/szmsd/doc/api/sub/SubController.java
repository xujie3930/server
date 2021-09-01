package com.szmsd.doc.api.sub;

import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.doc.api.sub.response.SubResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author francis
 * @date 2021-09-01
 */
@Api(tags = {"主子类别"})
@ApiSort(100)
@RestController
@RequestMapping("/api/sub/")
public class SubController {

    @Autowired
    private BasSubClientService subClientService;

    @ApiOperation("根据主类别code 查询子类别List")
    @GetMapping("getSubList")
    public R getSubList(@RequestParam("mainCode") String mainCode){
        if (StringUtils.isEmpty(mainCode)) {
            return R.failed("主类别编码不能为空");
        }
        List<BasSub> subList = subClientService.listApi(mainCode, "");
        if(subList != null) {
            return R.ok(BeanMapperUtil.mapList(subList, SubResponse.class));
        }
        return R.ok();
    }
}
