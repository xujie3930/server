package com.szmsd.system.controller;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.system.domain.SysNotice;
import com.szmsd.system.service.ISysNoticeService;

import javax.annotation.Resource;

/**
 * 公告 信息操作处理
 * 
 * @author lzw
 */
@RestController
@RequestMapping("/notice")
@Api(tags = "公告 信息操作处理")
public class SysNoticeController extends BaseController
{
    @Resource
    private ISysNoticeService noticeService;

    /**
     * 获取通知公告列表
     */
    @PreAuthorize("@ss.hasPermi('system:notice:list')")
    @GetMapping("/list")
    @ApiOperation(httpMethod = "GET", value = "获取通知公告列表")
    public TableDataInfo list(SysNotice notice)
    {
        startPage();
        List<SysNotice> list = noticeService.selectNoticeList(notice);
        return getDataTable(list);
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:notice:query')")
    @GetMapping(value = "/{noticeId}")
    @ApiOperation(httpMethod = "GET", value = "根据通知公告编号获取详细信息")
    public R getInfo(@PathVariable Long noticeId)
    {
        return R.ok(noticeService.selectNoticeById(noticeId));
    }

    /**
     * 新增通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:add')")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PostMapping
    @ApiOperation(httpMethod = "POST", value = "新增通知公告")
    public R add(@Validated @RequestBody SysNotice notice)
    {
        notice.setCreateByName(SecurityUtils.getUsername());
        return toOk(noticeService.insertNotice(notice));
    }

    /**
     * 修改通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:edit')")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(httpMethod = "PUT", value = "修改通知公告")
    public R edit(@Validated @RequestBody SysNotice notice)
    {
        notice.setUpdateByName(SecurityUtils.getUsername());
        return toOk(noticeService.updateNotice(notice));
    }

    /**
     * 删除通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:remove')")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    @ApiOperation(httpMethod = "DELETE", value = "删除通知公告")
    public R remove(@PathVariable Long[] noticeIds)
    {
        return toOk(noticeService.deleteNoticeByIds(noticeIds));
    }
}
