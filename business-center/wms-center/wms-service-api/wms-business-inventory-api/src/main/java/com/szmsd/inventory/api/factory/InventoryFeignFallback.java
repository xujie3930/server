package com.szmsd.inventory.api.factory;

import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.inventory.api.feign.InventoryFeignService;
import com.szmsd.inventory.domain.dto.InboundInventoryDTO;
import com.szmsd.inventory.domain.dto.InventoryAvailableQueryDto;
import com.szmsd.inventory.domain.dto.InventoryOperateListDto;
import com.szmsd.inventory.domain.dto.InventorySkuVolumeQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import com.szmsd.inventory.domain.vo.InventorySkuVolumeVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryFeignFallback implements FallbackFactory<InventoryFeignService> {
    @Override
    public InventoryFeignService create(Throwable throwable) {
        return new InventoryFeignService() {
            @Override
            public R inbound(InboundInventoryDTO receivingRequest) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<InventorySkuVolumeVO>> querySkuVolume(InventorySkuVolumeQueryDTO inventorySkuVolumeQueryDTO) {
                return R.convertResultJson(throwable);
            }

            @Override
            public TableDataInfo<InventoryAvailableListVO> queryAvailableList(InventoryAvailableQueryDto queryDto) {
                if (null == throwable) {
                    return null;
                }
                if (throwable instanceof CommonException) {
                    CommonException commonException = (CommonException) throwable;
                    throw new CommonException("" + Constants.FAIL, commonException.getMessage());
                } else {
                    throw new CommonException("" + Constants.FAIL, throwable.getMessage());
                }
            }

            @Override
            public R<Integer> freeze(InventoryOperateListDto operateListDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> unFreeze(InventoryOperateListDto operateListDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> unFreezeAndFreeze(InventoryOperateListDto operateListDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> deduction(InventoryOperateListDto operateListDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> unDeduction(InventoryOperateListDto operateListDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> unDeductionAndDeduction(InventoryOperateListDto operateListDto) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
