package com.szmsd.putinstorage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.putinstorage.component.RemoteComponent;
import com.szmsd.putinstorage.domain.InboundReceiptDetail;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDetailDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDetailQueryDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptDetailVO;
import com.szmsd.putinstorage.mapper.InboundReceiptDetailMapper;
import com.szmsd.putinstorage.service.IInboundReceiptDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InboundReceiptDetailServiceImpl extends ServiceImpl<InboundReceiptDetailMapper, InboundReceiptDetail> implements IInboundReceiptDetailService {

    @Resource
    private RemoteComponent remoteComponent;

    /**
     * 查询入库单明细信息 - 包含附件
     * @param queryDto
     * @return
     */
    @Override
    public List<InboundReceiptDetailVO> selectList(InboundReceiptDetailQueryDTO queryDto) {
        return selectList(queryDto, true);
    }

    /**
     * 查询入库单明细信息
     * @param queryDto
     * @param isContainFile 是否包含附件
     * @return
     */
    @Override
    public List<InboundReceiptDetailVO> selectList(InboundReceiptDetailQueryDTO queryDto, boolean isContainFile) {
        List<InboundReceiptDetailVO> inboundReceiptDetailVOS = baseMapper.selectList(queryDto);
        if (isContainFile) {
            inboundReceiptDetailVOS.forEach(inboundReceiptDetailVO -> {
                List<BasAttachment> attachment = remoteComponent.getAttachment(new BasAttachmentQueryDTO().setAttachmentType(AttachmentTypeEnum.INBOUND_RECEIPT_EDITION_IMAGE.getAttachmentType()).setBusinessNo(inboundReceiptDetailVO.getWarehouseNo()).setBusinessItemNo(inboundReceiptDetailVO.getId().toString()));
                if (CollectionUtils.isNotEmpty(attachment)) {
                    inboundReceiptDetailVO.setEditionImage(new AttachmentFileDTO().setId(attachment.get(0).getId()).setAttachmentName(attachment.get(0).getAttachmentName()).setAttachmentUrl(attachment.get(0).getAttachmentUrl()));
                }
            });
        }
        return inboundReceiptDetailVOS;
    }

    @Override
    public InboundReceiptDetailVO selectObject(String warehouseNo, String sku) {
        List<InboundReceiptDetailVO> inboundReceiptDetailVOS = this.selectList(new InboundReceiptDetailQueryDTO().setWarehouseNo(warehouseNo).setSku(sku));
        return CollectionUtils.isNotEmpty(inboundReceiptDetailVOS) ? inboundReceiptDetailVOS.get(0) : null;
    }

    /**
     * 保存入库单明细
     * @param inboundReceiptDetailDTOS
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveInboundReceiptDetail(List<InboundReceiptDetailDTO> inboundReceiptDetailDTOS) {
        log.info("保存入库单明细：SIZE={}", inboundReceiptDetailDTOS.size());

        // 是否有重复的sku
        Map<String, Long> collect = inboundReceiptDetailDTOS.stream().map(InboundReceiptDetailDTO::getSku).collect(Collectors.groupingBy(p -> p, Collectors.counting()));
        collect.entrySet().forEach(item -> AssertUtil.isTrue(!(item.getValue() > 1L), "入库单明细存在重复SKU"));

        inboundReceiptDetailDTOS.forEach(this::saveInboundReceiptDetail);
        log.info("保存入库单明细：操作成功");
    }

    @Override
    public InboundReceiptDetail saveInboundReceiptDetail(InboundReceiptDetailDTO inboundReceiptDetailDTO) {
        log.info("保存入库单明细：{}", inboundReceiptDetailDTO);

        // 验证SKU
        remoteComponent.vailSku(inboundReceiptDetailDTO.getSku());

        Integer declareQty = inboundReceiptDetailDTO.getDeclareQty();
        AssertUtil.isTrue(declareQty > 0, "SKU[" + inboundReceiptDetailDTO.getSku() + "]申报数量不能为" + declareQty);

        InboundReceiptDetail inboundReceiptDetail = BeanMapperUtil.map(inboundReceiptDetailDTO, InboundReceiptDetail.class);
        baseMapper.insert(inboundReceiptDetail);
        // 保存附件
        asyncAttachment(inboundReceiptDetail.getId(), inboundReceiptDetailDTO);
        return inboundReceiptDetail;
    }

    /**
     * #B1 接收入库上架 修改明细上架数量
     * @param warehouseNo 入库单
     * @param sku SKU
     * @param qty 数量
     */
    @Override
    public void receiving(String warehouseNo, String sku, Integer qty) {
        InboundReceiptDetailVO inboundReceiptDetailVO = this.selectObject(warehouseNo, sku);
        AssertUtil.notNull(inboundReceiptDetailVO, "入库单[" + warehouseNo + "]，不存在SKU[" + sku + "]明细，请核对");
        Integer beforeOutQty = inboundReceiptDetailVO.getPutQty();
        InboundReceiptDetail inboundReceiptDetail = new InboundReceiptDetail().setId(inboundReceiptDetailVO.getId());
        inboundReceiptDetail.setPutQty(beforeOutQty + qty);
        this.updateById(inboundReceiptDetail);
    }

    /**
     * 异步保存附件
     * 空对象不会调用远程接口，空数组会删除所对应的附件
     * @param inboundReceiptDetailId
     * @param inboundReceiptDetail
     */
    private void asyncAttachment(Long inboundReceiptDetailId, InboundReceiptDetailDTO inboundReceiptDetail) {
        CompletableFuture.runAsync(() -> {
            AttachmentFileDTO editionImage = inboundReceiptDetail.getEditionImage();
            if (editionImage != null) {
                log.info("保存单证信息文件");
                remoteComponent.saveAttachment(inboundReceiptDetail.getWarehouseNo(), inboundReceiptDetailId.toString(), Arrays.asList(editionImage), AttachmentTypeEnum.INBOUND_RECEIPT_EDITION_IMAGE);
            }
        });
    }
}

