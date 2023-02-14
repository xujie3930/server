package com.szmsd.track.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.track.domain.TrackConfigRule;
import com.szmsd.track.service.TrackConfigruleService;
import com.szmsd.track.vo.TrackConfigRuleQueryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 轨迹配置
 * </p>
 *
 * @author wxf
 * @since 2023-02-14
 */
@Api(tags = {"轨迹配置"})
@Controller
@RequestMapping("/track-config-rule")
public class TrackConfigruleController extends BaseController {

    @Autowired
    private TrackConfigruleService configruleService;

    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping(value = "/")
    public TableDataInfo<TrackConfigRule> list(TrackConfigRuleQueryVO trackConfigRuleQueryVO) {

        List<TrackConfigRule> trackConfigRules = configruleService.selectPage(trackConfigRuleQueryVO);

        return getDataTable(trackConfigRules);
    }

    @ApiOperation(value = "根据ID获取详情", notes = "根据ID获取详情")
    @GetMapping(value = "/{id}")
    public R<TrackConfigRule> getById(@PathVariable("id") String id) {
        return R.ok(configruleService.getById(id));
    }

    @ApiOperation(value = "保存配置", notes = "保存配置")
    @PostMapping(value = "/create")
    public R create(@RequestBody TrackConfigRule params) {
        configruleService.save(params);
        return R.ok();
    }

    @ApiOperation(value = "删除配置", notes = "删除配置")
    @PostMapping(value = "/delete/{id}")
    public R delete(@PathVariable("id") String id) {
        configruleService.removeById(id);
        return R.ok();
    }

    @ApiOperation(value = "更新配置", notes = "更新配置")
    @PostMapping(value = "/update")
    public ResponseEntity<Object> delete(@RequestBody TrackConfigRule params) {
        configruleService.updateById(params);
        return new ResponseEntity<>("updated successfully", HttpStatus.OK);
    }
}
