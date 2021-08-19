package com.szmsd.doc.api.warehouse;

import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.dto.BasWarehouseQueryDTO;
import com.szmsd.bas.vo.BasWarehouseVO;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.doc.api.warehouse.resp.BasWarehouseQueryReq;
import com.szmsd.doc.api.warehouse.resp.BasWarehouseResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"仓库信息"})
@RestController
@RequestMapping("/api/bas")
public class BaseWarehouseApiController extends BaseController {

    @Resource
    private BasWarehouseClientService basWarehouseClientService;

    /**
     * 查询 仓库列表
     *
     * @param queryDTO
     * @return
     */
    @PreAuthorize("hasAuthority('read')")
    @PostMapping("/warehouse/page")
    @ApiOperation(value = "仓库列表-分页查询", notes = "用于在创建入库单、出库单时选择仓库，支持分页")
    public TableDataInfo<BasWarehouseResp> pagePost(@Validated @RequestBody BasWarehouseQueryReq queryDTO) {
        BasWarehouseQueryDTO basWarehouseQueryReq = new BasWarehouseQueryDTO();
        BeanUtils.copyProperties(queryDTO, basWarehouseQueryReq);
        TableDataInfo<BasWarehouseVO> basWarehousePage = basWarehouseClientService.queryByWarehouseCodes(basWarehouseQueryReq);
        List<BasWarehouseVO> rows = basWarehousePage.getRows();
        List<BasWarehouseResp> collect = rows.stream().map(BasWarehouseResp::convertThis).collect(Collectors.toList());
        long total = basWarehousePage.getTotal();
        TableDataInfo<BasWarehouseResp> tableDataInfo = new TableDataInfo(collect, (int) total);
        return tableDataInfo;
    }

}
