package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasVercode;
import com.szmsd.bas.service.IBasVercodeService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 短信发送表 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-09-10
 */

@Api(tags = {"短信发送表"})
@RestController
@RequestMapping("/bas-vercode")
public class BasVercodeController extends BaseController {


    @Resource
    private IBasVercodeService basVercodeService;

    /**
     * 查询短信发送表模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basvercode:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasVercode basVercode) {
        startPage();
        List<BasVercode> list = basVercodeService.selectBasVercodeList(basVercode);
        return getDataTable(list);
    }


    /**
     * 新增短信发送表模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basvercode:add')")
    @Log(title = "短信发送表模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BasVercode basVercode) {
        return toOk(basVercodeService.insertBasVercode(basVercode));
    }

    /**
     * 修改短信发送表模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basvercode:edit')")
    @Log(title = "短信发送表模块", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BasVercode basVercode) {
        return toOk(basVercodeService.updateBasVercode(basVercode));
    }

    /**
     * 删除短信发送表模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basvercode:remove')")
    @Log(title = "短信发送表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basVercodeService.deleteBasVercodeByIds(ids));
    }

}
