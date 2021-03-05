package com.szmsd.putinstorage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.putinstorage.component.RemoteComponent;
import com.szmsd.putinstorage.component.RemoteRequest;
import com.szmsd.putinstorage.domain.InboundReceipt;
import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDetailDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDetailQueryDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptQueryDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptDetailVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;
import com.szmsd.putinstorage.mapper.InboundReceiptMapper;
import com.szmsd.putinstorage.service.IInboundReceiptDetailService;
import com.szmsd.putinstorage.service.IInboundReceiptService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class InboundReceiptServiceImpl extends ServiceImpl<InboundReceiptMapper, InboundReceipt> implements IInboundReceiptService {

    @Resource
    private RemoteRequest remoteRequest;

    @Resource
    private IInboundReceiptDetailService iInboundReceiptDetailService;

    @Resource
    private RemoteComponent remoteComponent;

    /**
     * 入库单查询
     * @param queryDTO
     * @return
     */
    @Override
    public List<InboundReceiptVO> selectList(InboundReceiptQueryDTO queryDTO) {
        return baseMapper.selectList(queryDTO);
    }

    /**
     * 创建入库单
     * @param createInboundReceiptDTO
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void create(CreateInboundReceiptDTO createInboundReceiptDTO) {
        log.info("创建入库单：{}", createInboundReceiptDTO);

        Integer totalDeclareQty = createInboundReceiptDTO.getTotalDeclareQty();
        AssertUtil.isTrue(totalDeclareQty > 0, "合计申报数量不能为" + totalDeclareQty);

        // 保存入库单
        InboundReceipt inboundReceipt = this.saveInboundReceipt(createInboundReceiptDTO);

        // 保存入库单明细
        List<InboundReceiptDetailDTO> inboundReceiptDetailDTOS = createInboundReceiptDTO.getInboundReceiptDetailDTOS();
        inboundReceiptDetailDTOS.forEach(item -> item.setWarehouseNo(inboundReceipt.getWarehouseNo()));
        iInboundReceiptDetailService.saveInboundReceiptDetail(inboundReceiptDetailDTOS);

        // 第三方接口推送
//        CreateReceiptRequest createReceiptRequest = new CreateReceiptRequest(createInboundReceiptDTO);
//        remoteRequest.createInboundReceipt(createReceiptRequest);

        log.info("创建入库单：操作完成");
    }

    /**
     * 保存入库单信息
     * @param inboundReceiptDTO
     * @return
     */
    @Override
    public InboundReceipt saveInboundReceipt(InboundReceiptDTO inboundReceiptDTO) {
        log.info("保存入库单：{}", inboundReceiptDTO);

        InboundReceipt inboundReceipt = BeanMapperUtil.map(inboundReceiptDTO, InboundReceipt.class);
        // 获取入库单号
        String warehouseNo = inboundReceipt.getWarehouseNo();
        if (StringUtils.isEmpty(warehouseNo)) {
            warehouseNo = remoteComponent.getWarehouseNo(inboundReceiptDTO.getCusCode());
        }
        inboundReceipt.setWarehouseNo(warehouseNo);
        baseMapper.insert(inboundReceipt);

        // 保存附件
        asyncAttachment(warehouseNo, inboundReceiptDTO);

        log.info("保存入库单：操作完成");
        return inboundReceipt;
    }

    /**
     * 入库单详情
     * @param warehouseNo
     * @return
     */
    @Override
    public InboundReceiptInfoVO queryInfo(String warehouseNo) {
        log.info("查询入库单详情：warehouseNo={}", warehouseNo);
        InboundReceiptInfoVO inboundReceiptInfoVO = baseMapper.selectInfo(null, warehouseNo);
        if (inboundReceiptInfoVO == null) {
            return null;
        }
        // 差明细
        List<InboundReceiptDetailVO> inboundReceiptDetailVOS = iInboundReceiptDetailService.selectList(new InboundReceiptDetailQueryDTO().setWarehouseNo(warehouseNo));
        inboundReceiptInfoVO.setInboundReceiptDetailVOS(inboundReceiptDetailVOS);
        log.info("查询入库单详情：查询完成{}", inboundReceiptDetailVOS);
        return inboundReceiptInfoVO;
    }


    /**
     * 异步保存附件
     * 空对象不会调用远程接口，空数组会删除所对应的附件
     * @param warehouseNo
     * @param inboundReceiptDTO
     */
    private void asyncAttachment(String warehouseNo, InboundReceiptDTO inboundReceiptDTO) {
        CompletableFuture.runAsync(() -> {
            AttachmentDataDTO documentsFile = inboundReceiptDTO.getDocumentsFile();
            if (documentsFile != null) {
                log.info("保存单证信息文件");
                remoteComponent.saveAttachment(warehouseNo, Arrays.asList(documentsFile), AttachmentTypeEnum.INBOUND_RECEIPT_DOCUMENTS);
            }
        });
    }

}

