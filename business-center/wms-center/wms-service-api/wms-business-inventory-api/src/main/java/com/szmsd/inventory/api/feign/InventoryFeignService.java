package com.szmsd.inventory.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.inventory.api.BusinessInventoryInterface;
import com.szmsd.inventory.api.factory.InventoryFeignFallback;
import com.szmsd.inventory.domain.dto.InboundInventoryDTO;
import com.szmsd.inventory.domain.dto.InventoryAvailableQueryDto;
import com.szmsd.inventory.domain.dto.InventoryOperateListDto;
import com.szmsd.inventory.domain.dto.InventorySkuVolumeQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import com.szmsd.inventory.domain.vo.InventorySkuVolumeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "FeignClient.InventoryFeignService", name = BusinessInventoryInterface.SERVICE_NAME, fallbackFactory = InventoryFeignFallback.class)
public interface InventoryFeignService {

    @PostMapping("/inventory/inbound")
    R inbound(@RequestBody InboundInventoryDTO receivingRequest);

    @PostMapping("/inventory/skuVolume")
    R<List<InventorySkuVolumeVO>> querySkuVolume(@RequestBody InventorySkuVolumeQueryDTO inventorySkuVolumeQueryDTO);

    @PostMapping("/inventory/queryAvailableList")
    TableDataInfo<InventoryAvailableListVO> queryAvailableList(@RequestBody InventoryAvailableQueryDto queryDto);

    @PostMapping("/inventory/freeze")
    R<Integer> freeze(@RequestBody InventoryOperateListDto operateListDto);

    @PostMapping("/inventory/unFreeze")
    R<Integer> unFreeze(@RequestBody InventoryOperateListDto operateListDto);

    @PostMapping("/inventory/unFreezeAndFreeze")
    R<Integer> unFreezeAndFreeze(@RequestBody InventoryOperateListDto operateListDto);

    @PostMapping("/inventory/deduction")
    R<Integer> deduction(@RequestBody InventoryOperateListDto operateListDto);

    @PostMapping("/inventory/unDeduction")
    R<Integer> unDeduction(@RequestBody InventoryOperateListDto operateListDto);

    @PostMapping("/inventory/unDeductionAndDeduction")
    R<Integer> unDeductionAndDeduction(@RequestBody InventoryOperateListDto operateListDto);
}
