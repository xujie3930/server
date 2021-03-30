package com.szmsd.bas.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.bas.domain.dto.basSubDto;
import com.szmsd.bas.driver.UpdateRedis;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.bas.service.IBasSubService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.CodeToNameEnum;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-06-18
 */

@Api(tags = {"子类别模块"})
@RestController
@RequestMapping("/bas-sub")
public class BasSubController extends BaseController {


    @Resource
    private IBasSubService basSubService;

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:bassub:list')")
    @ApiOperation(value = "查询子类别列表", notes = "查询子类别列表")
    @GetMapping("/list")
    public TableDataInfo list(BasSub basSub) {
        startPage();
        List<BasSub> list = basSubService.selectBasSubList(basSub);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('bas:bassub:listApi')")
    @ApiOperation(value = "查询子类别列表api", notes = "查询子类别列表api")
    @GetMapping("/listApi")
    public List<BasSub> listApi(@RequestParam("mainCode") String mainCode,@RequestParam("subValue") String subValue) {
        return basSubService.selectBasSubList(new BasSub(mainCode,subValue));
    }

    @ApiOperation(value = "根据code查询子类别（下拉框）")
    @RequestMapping("/getSub")
    public R<Map<String, List<BasSubWrapperVO>>> getSub(@RequestParam("code") String code) {
        List<String> codes = new ArrayList<>();
        if (code.contains(",")) {
            codes.addAll(Arrays.asList(code.split(",")));
        } else {
            codes.add(code);
        }
        QueryWrapper<BasSub> queryWrapper = Wrappers.query();
        queryWrapper.select("main_code", "sub_code", "sub_value", "sub_name", "sub_name_en");
        queryWrapper.in("main_code", codes);
        List<BasSub> list = this.basSubService.list(queryWrapper);
        Map<String, List<BasSubWrapperVO>> map;
        if (CollectionUtils.isEmpty(list)) {
            map = Collections.emptyMap();
        } else {
            map = new HashMap<>();
            for (BasSub basSub : list) {
                String mainCode = basSub.getMainCode();
                if (map.containsKey(mainCode)) {
                    map.get(mainCode).add(BeanMapperUtil.map(basSub, BasSubWrapperVO.class));
                } else {
                    List<BasSubWrapperVO> voList = new ArrayList<>();
                    voList.add(BeanMapperUtil.map(basSub, BasSubWrapperVO.class));
                    map.put(mainCode, voList);
                }
            }
        }
        return R.ok(map);
    }

    @ApiOperation(value = "根据name，code查询子类别（下拉框）", notes = "根据name，code查询子类别列表（下拉框）")
    @GetMapping("/getSubName")
    public R list(String code, String name) {
        BasSub basSub = new BasSub();
        JSONObject data = new JSONObject();
        if (code.contains(",")) {
            String[] split = code.split(",");
            List<String> asList = Arrays.asList(name.split(","));
            for (int i = 0; i < split.length; i++) {
                basSub.setMainCode(split[i]);
                List<BasSub> list = basSubService.selectBasSubList(basSub);
                if (getLen().equals("zh")) {
                    List<basSubDto> basSubDtoListZh = new ArrayList<>();
                    for (BasSub basSub1 : list) {
                        basSubDto basSubDto = new basSubDto();
                        basSubDto.setSubCode(basSub1.getSubCode());
                        basSubDto.setSubName(basSub1.getSubName());
                        basSubDto.setSubValue(basSub1.getSubValue());
                        basSubDto.setSort(basSub1.getSort());
                        basSubDto.setMainName(basSub1.getMainName());
                        basSubDto.setSubNameEn(basSub1.getSubNameEn());
                        basSubDto.setSubNameAr(basSub1.getSubNameAr());
                        basSubDto.setMainCode(basSub1.getMainCode());
                        basSubDtoListZh.add(basSubDto);
                    }
                    data.put(asList.get(i), basSubDtoListZh);
                } else if (getLen().equals("en")) {
                    List<basSubDto> basSubDtoListEn = new ArrayList<>();
                    for (BasSub basSub1 : list) {
                        basSubDto basSubDto = new basSubDto();
                        basSubDto.setSubCode(basSub1.getSubCode());
                        basSubDto.setSubName(basSub1.getSubNameEn());
                        basSubDto.setSubValue(basSub1.getSubValue());
                        basSubDto.setSort(basSub1.getSort());
                        basSubDto.setMainName(basSub1.getMainName());
                        basSubDto.setMainCode(basSub1.getMainCode());
                        basSubDto.setSubNameEn(basSub1.getSubNameEn());
                        basSubDto.setSubNameAr(basSub1.getSubNameAr());
                        basSubDtoListEn.add(basSubDto);
                    }
                    data.put(asList.get(i), basSubDtoListEn);
                } else {
                    List<basSubDto> basSubDtoListAr = new ArrayList<>();
                    for (BasSub basSub1 : list) {
                        basSubDto basSubDto = new basSubDto();
                        basSubDto.setSubCode(basSub1.getSubCode());
                        basSubDto.setSubName(basSub1.getSubNameAr());
                        basSubDto.setSubValue(basSub1.getSubValue());
                        basSubDto.setSort(basSub1.getSort());
                        basSubDto.setMainName(basSub1.getMainName());
                        basSubDto.setMainCode(basSub1.getMainCode());
                        basSubDto.setSubNameEn(basSub1.getSubNameEn());
                        basSubDto.setSubNameAr(basSub1.getSubNameAr());
                        basSubDtoListAr.add(basSubDto);
                    }
                    data.put(asList.get(i), basSubDtoListAr);
                }
            }
        } else {
            basSub.setMainCode(code);
            List<BasSub> baslist = basSubService.selectBasSubList(basSub);
            if (getLen().equals("zh")) {
                List<basSubDto> basSubDtoListZh = new ArrayList<>();
                for (BasSub basSub1 : baslist) {
                    basSubDto basSubDto = new basSubDto();
                    basSubDto.setSubCode(basSub1.getSubCode());
                    basSubDto.setSubName(basSub1.getSubName());
                    basSubDto.setSubValue(basSub1.getSubValue());
                    basSubDto.setSort(basSub1.getSort());
                    basSubDto.setMainName(basSub1.getMainName());
                    basSubDto.setMainCode(basSub1.getMainCode());
                    basSubDto.setSubNameEn(basSub1.getSubNameEn());
                    basSubDto.setSubNameAr(basSub1.getSubNameAr());
                    basSubDtoListZh.add(basSubDto);
                }
                data.put(name, basSubDtoListZh);
            } else if (getLen().equals("en")) {
                List<basSubDto> basSubDtoListEn = new ArrayList<>();
                for (BasSub basSub1 : baslist) {
                    basSubDto basSubDto = new basSubDto();
                    basSubDto.setSubCode(basSub1.getSubCode());
                    basSubDto.setSubName(basSub1.getSubNameEn());
                    basSubDto.setSubValue(basSub1.getSubValue());
                    basSubDto.setSort(basSub1.getSort());
                    basSubDto.setMainName(basSub1.getMainName());
                    basSubDto.setSubNameEn(basSub1.getSubNameEn());
                    basSubDto.setSubNameAr(basSub1.getSubNameAr());
                    basSubDto.setMainCode(basSub1.getMainCode());
                    basSubDtoListEn.add(basSubDto);
                }
                data.put(name, basSubDtoListEn);
            } else {
                List<basSubDto> basSubDtoListAr = new ArrayList<>();
                for (BasSub basSub1 : baslist) {
                    basSubDto basSubDto = new basSubDto();
                    basSubDto.setSubCode(basSub1.getSubCode());
                    basSubDto.setSubName(basSub1.getSubNameAr());
                    basSubDto.setSubValue(basSub1.getSubValue());
                    basSubDto.setSort(basSub1.getSort());
                    basSubDto.setMainName(basSub1.getMainName());
                    basSubDto.setSubNameEn(basSub1.getSubNameEn());
                    basSubDto.setSubNameAr(basSub1.getSubNameAr());
                    basSubDto.setMainCode(basSub1.getMainCode());
                    basSubDtoListAr.add(basSubDto);
                }
                data.put(name, basSubDtoListAr);
            }
        }
        return R.ok(data);
    }

    /**
     * 导出模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:bassub:export')")
    @ApiOperation(value = "导出子类别列表", notes = "导出子类别列表")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, BasSub basSub) throws IOException {
        List<BasSub> list = basSubService.selectBasSubList(basSub);
        ExcelUtil<BasSub> util = new ExcelUtil<BasSub>(BasSub.class);
        util.exportExcel(response, list, "BasSub");
    }

    /**
     * 获取模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('bas:bassub:query')")
    @ApiOperation(value = "查询子类别列表", notes = "查询子类别列表")
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(basSubService.selectBasSubById(id));
    }

    /**
     * 新增模块
     */
    @PreAuthorize("@ss.hasPermi('bas:bassub:add')")
    @ApiOperation(value = "新增子类别列表", notes = "新增子类别列表")
    @UpdateRedis(type = CodeToNameEnum.BAS_SUB)
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BasSub basSub) {
        if (basSub.getId() == null) {
            basSub.setCreateTime(new Date());
            BasSub basSub2 = new BasSub();
            String mainSub = basSub.getMainCode();
            if (StringUtils.isEmpty(mainSub)) {
                return R.failed(String.format("主类别不能为空:%s", mainSub));
            }
            basSub2.setMainCode(mainSub);
            List<BasSub> list = basSubService.selectBasSubList(basSub2);
            int subCode = 1;
            int length = 3;
            if (!CollectionUtils.isEmpty(list)) {
                String subStr = list.get(0).getSubCode().substring(mainSub.length());
                subCode = Integer.parseInt(subStr);
                subCode++;
                length = subStr.length();
            }
            String startZeroStr = String.format("%0" + length + "d", subCode);
            basSub.setSubCode(mainSub + startZeroStr);
            int i = basSubService.insertBasSub(basSub);
            log.info("新增子类别:{}", i);
        }
        return R.ok();
    }

    /**
     * 修改子类别列表
     */
    @PreAuthorize("@ss.hasPermi('bas:bassub:edit')")
    @UpdateRedis(type = CodeToNameEnum.BAS_SUB)
    @Log(title = "修改子类别列表", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改子类别列表", notes = "修改子类别列表")
    @PutMapping
    public R edit(@RequestBody BasSub basSub) {
        basSub.setUpdateTime(new Date());
        return toOk(basSubService.updateBasSub(basSub));
    }

    @PostMapping("/getsub")
    @ApiOperation(value = "查询子类别模块", notes = "查询子类别模块")
    public R<List<BasSub>> getsub(@RequestBody BasSub basSub) {
        List<BasSub> list = basSubService.selectBasSubList(basSub);
        return R.ok(list);
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('bas:bassub:remove')")
    @ApiOperation(value = "删除子类别列表", notes = "删除子类别列表")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @UpdateRedis(type = CodeToNameEnum.BAS_SUB)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basSubService.deleteBasSubByIds(ids));
    }

}
