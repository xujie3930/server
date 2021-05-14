package com.szmsd.delivery.imported;

import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.delivery.dto.DelOutboundDetailImportDto;
import com.szmsd.delivery.vo.DelOutboundDetailVO;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 21:49
 */
public class DelOutboundSkuImportContainer {

    private final String warehouseCode;
    private final List<DelOutboundDetailImportDto> dataList;
    private final ImportValidationData importValidationData;

    public DelOutboundSkuImportContainer(String warehouseCode, List<DelOutboundDetailImportDto> dataList,
                                         ImportValidationData importValidationData) {
        this.warehouseCode = warehouseCode;
        this.dataList = dataList;
        this.importValidationData = importValidationData;
    }

    public List<DelOutboundDetailVO> get() {
        List<DelOutboundDetailVO> voList = new ArrayList<>(this.dataList.size());
        // 重新把扣减的库存加回去
        for (DelOutboundDetailImportDto dto : this.dataList) {
            this.importValidationData.resetQty(this.warehouseCode, dto.getSku(), Math.toIntExact(dto.getQty()));
        }
        // 处理返回值
        for (DelOutboundDetailImportDto dto : this.dataList) {
            InventoryAvailableListVO available = importValidationData.get(this.warehouseCode, dto.getSku());
            DelOutboundDetailVO vo = BeanMapperUtil.map(available, DelOutboundDetailVO.class);
            vo.setQty(dto.getQty());
            voList.add(vo);
        }
        return voList;
    }
}
