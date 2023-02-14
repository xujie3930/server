package com.szmsd.track.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.track.domain.TrackConfig;
import com.szmsd.track.service.TrackConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 定义轨迹状态
 * </p>
 *
 * @author wxf
 * @since 2023-02-14
 */
@Controller
@RequestMapping("/track-config")
public class TrackConfigController extends BaseController {

    @Autowired
    private TrackConfigService configService;

    @GetMapping(value = "/")
    public R<Page<TrackConfig>> list(@RequestParam(required = false) Integer current, @RequestParam(required = false) Integer pageSize) {
        if (current == null) {
            current = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        Page<TrackConfig> aPage = configService.page(new Page<>(current, pageSize));
        return R.ok(aPage);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<TrackConfig> getById(@PathVariable("id") String id) {
        return new ResponseEntity<>(configService.getById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Object> create(@RequestBody TrackConfig params) {
        configService.save(params);
        return new ResponseEntity<>("created successfully", HttpStatus.OK);
    }

    @PostMapping(value = "/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") String id) {
        configService.removeById(id);
        return new ResponseEntity<>("deleted successfully", HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<Object> delete(@RequestBody TrackConfig params) {
        configService.updateById(params);
        return new ResponseEntity<>("updated successfully", HttpStatus.OK);
    }
}
