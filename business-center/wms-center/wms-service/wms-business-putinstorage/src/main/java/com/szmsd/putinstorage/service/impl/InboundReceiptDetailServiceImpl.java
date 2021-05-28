package com.szmsd.putinstorage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.putinstorage.component.CheckTag;
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
            inboundReceiptDetailVOS.forEach(item -> {
                List<BasAttachment> attachment = remoteComponent.getAttachment(new BasAttachmentQueryDTO().setAttachmentType(AttachmentTypeEnum.SKU_IMAGE.getAttachmentType()).setBusinessNo(item.getSku()));
                if (CollectionUtils.isNotEmpty(attachment)) {
                    item.setEditionImage(new AttachmentFileDTO().setId(attachment.get(0).getId()).setAttachmentName(attachment.get(0).getAttachmentName()).setAttachmentUrl(attachment.get(0).getAttachmentUrl()));
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
     * 保存、修改、删除明细单
     * @param inboundReceiptDetailDTOS 需要保存的数据
     * @param receiptDetailIds 需要删除的id
     */
    @Override
    public void saveOrUpdate(List<InboundReceiptDetailDTO> inboundReceiptDetailDTOS, List<String> receiptDetailIds) {

        // 删除明细
        if (CollectionUtils.isNotEmpty(receiptDetailIds)) {
            receiptDetailIds.forEach(detailId -> {
                InboundReceiptDetail inboundReceiptDetail = baseMapper.selectOne(new QueryWrapper<InboundReceiptDetail>().select("warehouse_no").eq("id", detailId));
                asyncDeleteAttachment(inboundReceiptDetail.getWarehouseNo(), detailId);
            });
        }
        this.removeByIds(receiptDetailIds);

        // 保存明细
        this.saveOrUpdate(inboundReceiptDetailDTOS);
    }

    /**
     * 保存入库单明细
     * @param inboundReceiptDetailDTOS
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveOrUpdate(List<InboundReceiptDetailDTO> inboundReceiptDetailDTOS) {
        log.info("保存入库单明细：SIZE={}", inboundReceiptDetailDTOS.size());

        // 是否有重复的sku
        if (CheckTag.get()) {
            //转运单没有sku 故不校验 0526
            log.info("转运单没有sku 跳过校验是否有重复的sku");
        } else {
            Map<String, Long> collect = inboundReceiptDetailDTOS.stream().map(InboundReceiptDetailDTO::getSku).collect(Collectors.groupingBy(p -> p, Collectors.counting()));
            collect.entrySet().forEach(item -> AssertUtil.isTrue(!(item.getValue() > 1L), "入库单明细存在重复SKU"));
        }

        inboundReceiptDetailDTOS.forEach(this::saveOrUpdate);
        log.info("保存入库单明细：操作成功");
    }

    @Override
    public InboundReceiptDetail saveOrUpdate(InboundReceiptDetailDTO inboundReceiptDetailDTO) {
        log.info("保存入库单明细：{}", inboundReceiptDetailDTO);

        // 验证SKU
        remoteComponent.vailSku(inboundReceiptDetailDTO.getSku());

        Integer declareQty = inboundReceiptDetailDTO.getDeclareQty();
        AssertUtil.isTrue(declareQty > 0, "SKU[" + inboundReceiptDetailDTO.getSku() + "]申报数量不能为" + declareQty);

        InboundReceiptDetail inboundReceiptDetail = BeanMapperUtil.map(inboundReceiptDetailDTO, InboundReceiptDetail.class);
        this.saveOrUpdate(inboundReceiptDetail);

        if (inboundReceiptDetailDTO.getEditionImage() == null) {
            asyncDeleteAttachment(inboundReceiptDetailDTO.getWarehouseNo(), inboundReceiptDetail.getId() + "");
        } else {
            // 保存附件
            asyncAttachment(inboundReceiptDetail.getId(), inboundReceiptDetailDTO);
        }

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
     * 删除入库明细单 物理删除
     * @param warehouseNo
     */
    @Override
    public void deleteByWarehouseNo(String warehouseNo) {
        log.info("删除入库明细单：warehouseNo={}", warehouseNo);
        baseMapper.deleteByWarehouseNo(warehouseNo);
        log.info("删除入库明细单：删除完成");
    }

    /**
     * 删除入库明细单、附件 物理删除
     * @param warehouseNo
     */
    @Override
    public void deleteAndFileByWarehouseNo(String warehouseNo) {
        this.deleteByWarehouseNo(warehouseNo);
        asyncDeleteAttachment(warehouseNo, null);
    }

    /**
     * 异步删除附件
     * @param warehouseNo
     */
    private void asyncDeleteAttachment(String warehouseNo, String warehouseItemNo) {
        CompletableFuture.runAsync(() -> {
            AttachmentTypeEnum inboundReceiptDocuments = AttachmentTypeEnum.INBOUND_RECEIPT_EDITION_IMAGE;
            log.info("删除入库单[{}]{}", warehouseNo, inboundReceiptDocuments.getAttachmentType());
            remoteComponent.deleteAttachment(inboundReceiptDocuments, warehouseNo, warehouseItemNo);
        });
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
                AttachmentTypeEnum inboundReceiptEditionImage = AttachmentTypeEnum.INBOUND_RECEIPT_EDITION_IMAGE;
                log.info("保存{}[{}]", inboundReceiptEditionImage.getAttachmentType(), editionImage);
                remoteComponent.saveAttachment(inboundReceiptDetail.getWarehouseNo(), inboundReceiptDetailId.toString(), Arrays.asList(editionImage), inboundReceiptEditionImage);
            }
        });
    }
}

