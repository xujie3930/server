package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasWarehouseFeignService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.dto.AddWarehouseRequest;
import com.szmsd.bas.dto.BasWarehouseQueryDTO;
import com.szmsd.bas.vo.BasWarehouseVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.web.page.TableDataInfo;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class BasWarehouseFeignFallback implements FallbackFactory<BasWarehouseFeignService> {

    @Override
    public BasWarehouseFeignService create(Throwable throwable) {
        return new BasWarehouseFeignService() {
            @Override
            public R saveOrUpdate(AddWarehouseRequest addWarehouseRequest) {
                log.info("创建/更新仓库失败: {}", throwable.getMessage());
                return R.convertResultJson(throwable);
            }

            @Override
            public R<BasWarehouse> queryByWarehouseCode(String warehouseCode) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<BasWarehouse>> queryByWarehouseCodes(List<String> warehouseCodes) {
                return R.convertResultJson(throwable);
            }

            @Override
            public TableDataInfo<BasWarehouseVO> pagePost(BasWarehouseQueryDTO queryDTO) {
               throw new BaseException("查询仓库信息失败!");
            }
        };
    }
}
