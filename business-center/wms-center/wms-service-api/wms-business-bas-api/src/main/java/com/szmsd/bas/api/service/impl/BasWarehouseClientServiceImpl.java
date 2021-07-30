package com.szmsd.bas.api.service.impl;

import com.szmsd.bas.api.feign.BasWarehouseFeignService;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.dto.AddWarehouseRequest;
import com.szmsd.bas.dto.BasWarehouseQueryDTO;
import com.szmsd.bas.vo.BasWarehouseVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BasWarehouseClientServiceImpl implements BasWarehouseClientService {

    @Resource
    private BasWarehouseFeignService basWarehouseFeignService;

    /**
     * 创建/更新仓库
     *
     * @param addWarehouseRequest
     */
    @Override
    public R saveOrUpdate(AddWarehouseRequest addWarehouseRequest) {
        return basWarehouseFeignService.saveOrUpdate(addWarehouseRequest);
    }

    @Override
    public BasWarehouse queryByWarehouseCode(String warehouseCode) {
        return R.getDataAndException(this.basWarehouseFeignService.queryByWarehouseCode(warehouseCode));
    }

    @Override
    public List<BasWarehouse> queryByWarehouseCodes(List<String> warehouseCodes) {
        return R.getDataAndException(this.basWarehouseFeignService.queryByWarehouseCodes(warehouseCodes));
    }

    @Override
    public TableDataInfo<BasWarehouseVO> queryByWarehouseCodes(BasWarehouseQueryDTO queryDTO) {
        return this.basWarehouseFeignService.pagePost(queryDTO);
    }
}
