package com.szmsd.demo.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.demo.domain.Test;
import com.szmsd.demo.domain.TestDto;
import com.szmsd.demo.service.TestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 测试模块
 *
 * @author szmsd
 */
@RestController
@RequestMapping("/demo")
@Api(value = "/", description = "测试模块")
public class TestController extends BaseController {


    @Resource
    private TestService testService;

    /**
     * 查询测试模块列表
     */
//    @PreAuthorize("@ss.hasPermi('test:test:list')")
    @GetMapping("/list")
    @ApiOperation(httpMethod = "GET", value = "查询列表数据")
    public TableDataInfo list(Test test) {
        startPage();
        List<Test> list = testService.selectTestList(test);
        return getDataTable(list);
    }

    /**
     * 导出测试模板
     */
    @ApiOperation(httpMethod = "POST", value = "导出测试模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<Test> util = new ExcelUtil<Test>(Test.class);
        util.importTemplateExcel(response, "TEST数据");
    }

    /**
     * 导入数据
     */
//    @PreAuthorize("@ss.hasPermi('test:test:import')")
    @ApiOperation(httpMethod = "POST", value = "导入数据")
    @Log(title = "测试模块", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public R importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<Test> util = new ExcelUtil<Test>(Test.class);
        List<Test> testList = util.importExcel(file.getInputStream());
        String operName = SecurityUtils.getUsername();
        String message = testService.importTest(testList, updateSupport, operName);
        return R.ok(message);
    }

    /**
     * 导出数据
     */
//    @PreAuthorize("@ss.hasPermi('test:test:export')")
    @ApiOperation(httpMethod = "GET", value = "导出测试模块列表")
    @Log(title = "测试模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, Test test) throws IOException {
        List<Test> list = testService.selectTestList(test);
        ExcelUtil<Test> util = new ExcelUtil<Test>(Test.class);
        util.exportExcel(response, list, "test");
    }

    /**
     * 获取测试模块详细信息
     */
//    @PreAuthorize("@ss.hasPermi('test:test:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation(httpMethod = "GET", value = "获取测试模块详细信息")
    public R getInfo(@PathVariable("id") Long id) {
        return R.ok(testService.selectTestById(id));
    }

    /**
     * 新增测试模块 不需要
     */
//    @PreAuthorize("@ss.hasPermi('test:test:add')")
    @Log(title = "测试模块", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ApiOperation(httpMethod = "POST", value = "新增测试模块")
    public R add(@RequestBody TestDto testDto) {
        Test test=new Test();
        BeanUtils.copyBeanProp(test,testDto);
        return toOk(testService.insertTest(test));
    }

    /**
     * 修改测试模块
     */
//    @PreAuthorize("@ss.hasPermi('test:test:edit')")
    @Log(title = "测试模块", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    @ApiOperation(httpMethod = "PUT", value = "修改测试模块")
    public R edit(@RequestBody TestDto testDto)
    {
        Test test=new Test();
        BeanUtils.copyBeanProp(test,testDto);
        return toOk(testService.updateTest(test));
    }

    /**
     * 删除测试模块
     */
//    @PreAuthorize("@ss.hasPermi('test:test:remove')")
    @Log(title = "测试模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @ApiOperation(httpMethod = "DELETE", value = "删除测试模块")
    public R remove(@PathVariable List<Long> ids) {
        return toOk(testService.deleteTestByIds(ids));
    }


    /**
     * 多语言 全局返回成功
     */
    @PostMapping("/test1")
    @ApiOperation(httpMethod = "POST", value = "多语言 全局返回成功")
    public R getInfoSucess() {
        return R.ok();
    }

    /**
     * 多语言 全局返回成功 +数据
     */
    @PostMapping("/test2")
    @ApiOperation(httpMethod = "POST", value = "多语言 全局返回成功")
    public R test2() {
        return R.ok("44");
    }

    /**
     * 多语言 全局返回失败
     */
    @PostMapping("/test3")
    @ApiOperation(httpMethod = "POST", value = "多语言 全局返回失败")
    public R test3() {
        return R.failed();
    }

    /**
     * 多语言 全局返回失败，自定义错误信息枚举
     */
    @PostMapping("/test4")
    @ApiOperation(httpMethod = "POST", value = "多语言 全局返回失败")
    public R test4() {
        return R.failed(ExceptionMessageEnum.EXPSYSTEM001);
    }

    /**
     * 多语言 全局返回失败 自定义code+错误信息枚举
     */
    @PostMapping("/test5")
    @ApiOperation(httpMethod = "POST", value = "多语言 全局返回失败")
    public R test5() {
        return R.failed(203, ExceptionMessageEnum.EXPSYSTEM001);
    }


}
