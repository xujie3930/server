package com.szmsd.putinstorage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.putinstorage.component.RemoteComponent;
import com.szmsd.putinstorage.domain.InboundReceiptDetail;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDetailDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDetailQueryDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptDetailVO;
import com.szmsd.putinstorage.mapper.InboundReceiptDetailMapper;
import com.szmsd.putinstorage.service.IInboundReceiptDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class InboundReceiptDetailServiceImpl extends ServiceImpl<InboundReceiptDetailMapper, InboundReceiptDetail> implements IInboundReceiptDetailService {

    @Resource
    private RemoteComponent remoteComponent;

    @Override
    public List<InboundReceiptDetailVO> selectList(InboundReceiptDetailQueryDTO queryDto) {
        return null;
    }

    /**
     * 保存入库单明细
     * @param inboundReceiptDetailDTOS
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveInboundReceiptDetail(List<InboundReceiptDetailDTO> inboundReceiptDetailDTOS) {
        log.info("保存入库单明细：SIZE={}", inboundReceiptDetailDTOS.size());
        inboundReceiptDetailDTOS.forEach(this::saveInboundReceiptDetail);
        log.info("保存入库单明细：操作成功");
    }

    @Override
    public InboundReceiptDetail saveInboundReceiptDetail(InboundReceiptDetailDTO inboundReceiptDetailDTO) {
        log.info("保存入库单明细：{}", inboundReceiptDetailDTO);

        Object sku = remoteComponent.getSku(inboundReceiptDetailDTO.getSku());
        AssertUtil.notNull(sku, "SKU[" + sku + "]不存在");

        Integer declareQty = inboundReceiptDetailDTO.getDeclareQty();
        AssertUtil.isTrue(declareQty > 0, "SKU[" + sku + "]申报数量不能为" + declareQty);

        InboundReceiptDetail inboundReceiptDetail = BeanMapperUtil.map(inboundReceiptDetailDTO, InboundReceiptDetail.class);
        baseMapper.insert(inboundReceiptDetail);
        // 保存附件
        asyncAttachment(inboundReceiptDetail.getId(), inboundReceiptDetailDTO);
        return inboundReceiptDetail;
    }

    /**
     * 异步保存附件
     * 空对象不会调用远程接口，空数组会删除所对应的附件
     * @param inboundReceiptDetailId
     * @param inboundReceiptDetail
     */
    private void asyncAttachment(Long inboundReceiptDetailId, InboundReceiptDetailDTO inboundReceiptDetail) {
        CompletableFuture.runAsync(() -> {
            AttachmentDataDTO editionImage = inboundReceiptDetail.getEditionImage();
            if (editionImage != null) {
                log.info("保存单证信息文件");
                remoteComponent.saveAttachment(inboundReceiptDetail.getWarehouseNo(), inboundReceiptDetailId.toString(), Arrays.asList(editionImage), AttachmentTypeEnum.INBOUND_RECEIPT_EDITION_IMAGE);
            }
        });
    }
}

