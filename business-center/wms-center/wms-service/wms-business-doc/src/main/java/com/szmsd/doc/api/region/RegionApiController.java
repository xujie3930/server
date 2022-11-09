package com.szmsd.doc.api.region;

import com.szmsd.bas.api.domain.BasRegion;
import com.szmsd.bas.api.domain.dto.BasRegionQueryDTO;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.doc.api.region.request.RegionReq;
import com.szmsd.doc.api.region.response.RegionResp;
import com.szmsd.http.api.feign.HtpExceptionFeignService;
import com.szmsd.http.domain.BasCodExternal;
import com.szmsd.http.dto.BasCodExternalDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author francis
 * @date 2021-07-31
 */
@Api(tags = {"其他信息查询"})
@ApiSort(100)
@RestController
@RequestMapping("/api/region/")
public class RegionApiController {

    @Autowired
    private BasRegionFeignService basRegionFeignService;
    @Autowired
    private HtpExceptionFeignService htpExceptionFeignService;

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("list")
    @ApiOperation(value = "分页查询地区列表", notes = "查询地区列表，支持分页呈现")
    public TableDataInfo<RegionResp> list(@Validated @RequestBody RegionReq regionReq) {
        TableDataInfo<BasRegion> tableDataInfo = basRegionFeignService.postList(BeanMapperUtil.map(regionReq, BasRegionQueryDTO.class));
        TableDataInfo<RegionResp> regionResp = new TableDataInfo<>();
        BeanUtils.copyProperties(tableDataInfo, regionResp);
        List<BasRegion> rows = tableDataInfo.getRows();
        List<RegionResp> collect = rows.stream().map(x -> {
            RegionResp region = new RegionResp();
            BeanUtils.copyProperties(x, region);
            return region;
        }).collect(Collectors.toList());
        regionResp.setRows(collect);
        return regionResp;
    }

   // @PreAuthorize("hasAuthority('client')")
    @PostMapping("basCodlist")
    @ApiOperation(value = "分页查询COD汇率列表", notes = "查询COD汇率列表，支持分页呈现")
    public TableDataInfo<BasCodExternal> basCodlist(@RequestBody BasCodExternalDto basCodExternalDto) {
        R<TableDataInfo<BasCodExternal>> r=  htpExceptionFeignService.basCodlist(basCodExternalDto);
        return r.getData();
    }
}
