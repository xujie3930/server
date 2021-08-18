package com.szmsd.doc.api.warehouse;

import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.dto.BasWarehouseQueryDTO;
import com.szmsd.bas.vo.BasWarehouseVO;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    @ApiOperation(value = "仓库列表-分页查询", notes = "仓库列表 - 分页查询")
    public TableDataInfo<BasWarehouseVO> pagePost(@Validated @RequestBody BasWarehouseQueryDTO queryDTO) {
        return basWarehouseClientService.queryByWarehouseCodes(queryDTO);
    }

}
