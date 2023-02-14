package com.szmsd.track.controller;

import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.track.domain.TrackConfigRule;
import com.szmsd.track.service.TrackConfigruleService;
import com.szmsd.track.vo.TrackConfigRuleQueryVO;
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
@Controller
@RequestMapping("/track-config-rule")
public class TrackConfigruleController extends BaseController {

    @Autowired
    private TrackConfigruleService configruleService;

    @GetMapping(value = "/")
    public TableDataInfo<TrackConfigRule> list(TrackConfigRuleQueryVO trackConfigRuleQueryVO) {

        List<TrackConfigRule> trackConfigRules = configruleService.selectPage(trackConfigRuleQueryVO);

        return getDataTable(trackConfigRules);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<TrackConfigRule> getById(@PathVariable("id") String id) {
        return new ResponseEntity<>(configruleService.getById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Object> create(@RequestBody TrackConfigRule params) {
        configruleService.save(params);
        return new ResponseEntity<>("created successfully", HttpStatus.OK);
    }

    @PostMapping(value = "/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") String id) {
        configruleService.removeById(id);
        return new ResponseEntity<>("deleted successfully", HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<Object> delete(@RequestBody TrackConfigRule params) {
        configruleService.updateById(params);
        return new ResponseEntity<>("updated successfully", HttpStatus.OK);
    }
}
