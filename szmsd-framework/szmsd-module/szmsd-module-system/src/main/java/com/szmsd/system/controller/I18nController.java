//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.szmsd.system.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.system.api.domain.SysLang;
import com.szmsd.system.api.domain.dto.SysLangDTO;
import com.szmsd.system.api.task.I18nHandler;
import com.szmsd.system.config.I18nConfig;
import com.szmsd.system.domain.vo.NameValueAttributeVo;
import com.szmsd.system.service.I18nService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

@RestController
@RequestMapping({"/i18n"})
@Api(tags = "多语言")
public class I18nController extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private I18nService i18nService;

    @Autowired
    private I18nHandler i18nHandler;

    @Autowired
    private I18nConfig i18nConfig;

    public I18nController() {

    }

    @ApiOperation(value = "获取所有语言")
    @GetMapping("/getLangData")
    public R getLangData() {

        LinkedMultiValueMap<String, String> SYS_LANG_MAP = i18nHandler.getSysLangMap();
        List<NameValueAttributeVo> retList = new ArrayList();
        if (SYS_LANG_MAP != null && SYS_LANG_MAP.size() > 0) {

            int currentLangNumber = i18nHandler.getCurrentLangNumber();

            Iterator var5 = SYS_LANG_MAP.entrySet().iterator();

            while (var5.hasNext()) {
                Entry<String, List<String>> entry = (Entry) var5.next();
                String key = (String) entry.getKey();
                List<String> valueList = (List) entry.getValue();
                String langValue = (String) valueList.get(currentLangNumber);
                if (StringUtils.isEmpty(langValue)) {
                    langValue = key;
                }
                NameValueAttributeVo ent = new NameValueAttributeVo();
                ent.setName(key);
                ent.setValue(langValue);
                retList.add(ent);
            }
        }
        return R.ok(retList);
    }


    @ApiOperation(value = "获取全部列表-不分页")
    @PostMapping("/listAll")
    public R listAll(SysLangDTO sysLangDTO) {

        List<SysLang> list = this.i18nService.getTableData(sysLangDTO);
        return R.ok(list);
    }

    @ApiOperation(value = "获取某张表最新的操作时间")
    @PostMapping("/getTableUpdateTime")
    public R getTableUpdateTime(String tableName) {
        String updateTime = this.i18nService.getTableUpdateTime(tableName);
        return R.ok(updateTime);
    }


    @ApiOperation(
            httpMethod = "POST",
            value = "获取列表数据"
    )
    @PostMapping("/list")
    public TableDataInfo getTableData(@RequestBody SysLangDTO queryDto) throws Exception {
        startPage();
        List<SysLang> list = this.i18nService.getTableData(queryDto);
        return getDataTable(list);
    }

    @ApiOperation(
            httpMethod = "POST",
            value = "新增语言"
    )
    @PostMapping("/add")
    public R add(@RequestBody SysLang sysLang) {
        return R.ok();
        // try {
        //     ResultJson json = successResult();
        //     json.setMessage("新增语言成功");
        //     this.i18nService.insertTabSysLanres(sysLang);
        //     return json;
        // } catch (Exception var3) {
        //     return this.getErrorResultJson(var3.getMessage());
        // }
    }

    @ApiOperation(
            httpMethod = "POST",
            value = "修改语言"
    )
    @RequestMapping({"/update"})
    @ResponseBody
    public R update(@RequestBody SysLang sysLang) {
        return R.ok();
        // try {
        //     ResultJson json = new ResultJson();
        //     json.setMessage("修改语言成功");
        //     this.i18nService.updateTabSysLanres(sysLang);
        //     return json;
        // } catch (Exception var3) {
        //     return this.getErrorResultJson(var3.getMessage());
        // }
    }

    @ApiOperation(
            httpMethod = "POST",
            value = "删除语言"
    )
    @RequestMapping({"/delete"})
    @ResponseBody
    public R delete(@RequestParam("ids") String ids) {
        return R.ok();
        // try {
        //     ResultJson json = new ResultJson();
        //     json.setMessage("删除语言成功");
        //     this.i18nService.delTabSysLanres(ids);
        //     return json;
        // } catch (Exception var3) {
        //     return this.getErrorResultJson(var3.getMessage());
        // }
    }

}
