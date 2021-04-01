package com.szmsd.putinstorage.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.language.enums.LocalLanguageEnum;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.putinstorage.component.RemoteComponent;
import com.szmsd.putinstorage.component.RemoteRequest;
import com.szmsd.putinstorage.domain.InboundReceipt;
import com.szmsd.putinstorage.domain.dto.*;
import com.szmsd.putinstorage.domain.vo.InboundReceiptDetailVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;
import com.szmsd.putinstorage.enums.InboundReceiptEnum;
import com.szmsd.putinstorage.mapper.InboundReceiptMapper;
import com.szmsd.putinstorage.service.IInboundReceiptDetailService;
import com.szmsd.putinstorage.service.IInboundReceiptService;
import com.szmsd.system.api.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
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
     * 入库单号查询
     * @param warehouseNo
     * @return
     */
    @Override
    public InboundReceiptVO selectByWarehouseNo(String warehouseNo) {
        List<InboundReceiptVO> inboundReceiptVOS = this.selectList(new InboundReceiptQueryDTO().setWarehouseNo(warehouseNo));
        if (CollectionUtils.isNotEmpty(inboundReceiptVOS)) {
            return inboundReceiptVOS.get(0);
        }
        return null;
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
        String warehouseNo = inboundReceipt.getWarehouseNo();
        createInboundReceiptDTO.setWarehouseNo(warehouseNo);

        // 保存入库单明细
        List<InboundReceiptDetailDTO> inboundReceiptDetailDTOS = createInboundReceiptDTO.getInboundReceiptDetailDTOS();
        inboundReceiptDetailDTOS.forEach(item -> item.setWarehouseNo(warehouseNo));
        iInboundReceiptDetailService.saveInboundReceiptDetail(inboundReceiptDetailDTOS);

        // 判断自动审核
        boolean inboundReceiptReview = remoteComponent.inboundReceiptReview(createInboundReceiptDTO.getWarehouseCode());
        if (inboundReceiptReview) {
            // 审核
            String localLanguage = LocalLanguageEnum.getLocalLanguageSplice(LocalLanguageEnum.INBOUND_RECEIPT_REVIEW_0);
            this.review(new InboundReceiptReviewDTO().setWarehouseNo(warehouseNo).setStatus(InboundReceiptEnum.InboundReceiptStatus.REVIEW_PASSED.getValue()).setReviewRemark(localLanguage));

            // 第三方接口推送
            InboundReceiptInfoVO inboundReceiptInfoVO = this.queryInfo(warehouseNo, false);
            remoteRequest.createInboundReceipt(inboundReceiptInfoVO);
        }

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
     * 取消
     * @param warehouseNo
     */
    @Override
    public void cancel(String warehouseNo) {
        log.info("取消入库单：warehouseNo={}", warehouseNo);

        InboundReceiptVO inboundReceiptVO = this.selectByWarehouseNo(warehouseNo);
        AssertUtil.notNull(inboundReceiptVO, "入库单[" + warehouseNo + "]不存在");

        // 第三方接口推送
        remoteRequest.cancelInboundReceipt(inboundReceiptVO.getWarehouseNo(), inboundReceiptVO.getWarehouseName());

        // 修改为取消状态
        this.updateStatus(warehouseNo, InboundReceiptEnum.InboundReceiptStatus.CANCELLED);
        log.info("取消入库单：操作完成");
    }

    /**
     * 入库单详情
     * @param warehouseNo
     * @return
     */
    @Override
    public InboundReceiptInfoVO queryInfo(String warehouseNo) {
        log.info("查询入库单详情：warehouseNo={}", warehouseNo);
        InboundReceiptInfoVO inboundReceiptInfoVO = queryInfo(warehouseNo, true);
        return inboundReceiptInfoVO;
    }

    /**
     * 入库单详情
     * @param warehouseNo
     * @param isContainFile 是否包含明细附件
     * @return
     */
    public InboundReceiptInfoVO queryInfo(String warehouseNo, boolean isContainFile) {
        log.info("查询入库单详情：warehouseNo={}", warehouseNo);
        InboundReceiptInfoVO inboundReceiptInfoVO = baseMapper.selectInfo(null, warehouseNo);
        if (inboundReceiptInfoVO == null) {
            return null;
        }
        // 查明细
        List<InboundReceiptDetailVO> inboundReceiptDetailVOS = iInboundReceiptDetailService.selectList(new InboundReceiptDetailQueryDTO().setWarehouseNo(warehouseNo), isContainFile);
        inboundReceiptInfoVO.setInboundReceiptDetailVOS(inboundReceiptDetailVOS);
        log.info("查询入库单详情：查询完成{}", inboundReceiptDetailVOS);
        return inboundReceiptInfoVO;
    }

    /**
     * #B1 接收入库上架 修改上架数量
     * @param receivingRequest
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void receiving(ReceivingRequest receivingRequest) {
        log.info("#B1 接收入库上架：{}",  receivingRequest);

        Integer qty = receivingRequest.getQty();
        AssertUtil.isTrue(qty != null && qty > 0, "上架数量不能为" + qty);

        // 修改入库单明细中的上架数量
        String refOrderNo = receivingRequest.getOrderNo();
        InboundReceiptVO inboundReceiptVO = selectByWarehouseNo(refOrderNo);
        AssertUtil.notNull(inboundReceiptVO, "入库单号[" + refOrderNo + "]不存在，请核对");
        // 之前总上架数量
        Integer beforeTotalPutQty = inboundReceiptVO.getTotalPutQty();
        InboundReceipt inboundReceipt = new InboundReceipt().setId(inboundReceiptVO.getId());
        inboundReceipt.setTotalPutQty(beforeTotalPutQty + qty);
        // 第一次入库上架 把状态修改为 3处理中
        if (beforeTotalPutQty == 0) {
            inboundReceipt.setStatus(InboundReceiptEnum.InboundReceiptStatus.PROCESSING.getValue());
        }
        this.updateById(inboundReceipt);

        // 修改明细上架数量
        iInboundReceiptDetailService.receiving(receivingRequest.getOrderNo(), receivingRequest.getSku(), receivingRequest.getQty());

        // 库存 上架入库
        remoteComponent.inboundInventory(receivingRequest.setWarehouseCode(inboundReceiptVO.getWarehouseName()));

        log.info("#B1 接收入库上架：操作完成");
    }

    /**
     * #B3 接收完成入库
     * @param receivingCompletedRequest
     */
    @Override
    public void completed(ReceivingCompletedRequest receivingCompletedRequest) {
        log.info("#B3 接收完成入库：{}",  receivingCompletedRequest);
        updateStatus(receivingCompletedRequest.getOrderNo(), InboundReceiptEnum.InboundReceiptStatus.COMPLETED);
        log.info("#B3 接收完成入库：操作完成");

    }

    /**
     * 修改状态
     * @param warehouseNo
     * @param status
     */
    @Override
    public void updateStatus(String warehouseNo, InboundReceiptEnum.InboundReceiptStatus status) {
        InboundReceipt inboundReceipt = new InboundReceipt();
        inboundReceipt.setWarehouseNo(warehouseNo);
        inboundReceipt.setStatus(status.getValue());
        this.updateByWarehouseNo(inboundReceipt);
        log.info("入库单{}修改状态为:{}{}", warehouseNo, status.getValue(), status.getValue2());
    }

    @Override
    public void updateByWarehouseNo(InboundReceipt inboundReceipt) {
        this.update(inboundReceipt, new UpdateWrapper<InboundReceipt>().lambda().eq(InboundReceipt::getWarehouseNo, inboundReceipt.getWarehouseNo()));
    }

    /**
     * 入库单审核
     * @param inboundReceiptReviewDTO
     */
    @Override
    public void review(InboundReceiptReviewDTO inboundReceiptReviewDTO) {
        SysUser loginUserInfo = remoteComponent.getLoginUserInfo();
        InboundReceipt inboundReceipt = new InboundReceipt();
        String warehouseNo = inboundReceiptReviewDTO.getWarehouseNo();
        inboundReceipt.setWarehouseNo(warehouseNo);
        InboundReceiptEnum.InboundReceiptEnumMethods anEnum = InboundReceiptEnum.InboundReceiptEnumMethods.getEnum(InboundReceiptEnum.InboundReceiptStatus.class, inboundReceiptReviewDTO.getStatus());
        inboundReceipt.setStatus(anEnum.getValue());
        inboundReceipt.setReviewRemark(inboundReceiptReviewDTO.getReviewRemark());
        inboundReceipt.setReviewBy(loginUserInfo.getUserId() + "");
        inboundReceipt.setReviewBy(loginUserInfo.getUserName());
        inboundReceipt.setReviewTime(new Date());
        this.updateByWarehouseNo(inboundReceipt);
        log.info("入库单审核: {},{}", anEnum.getValue2(), inboundReceipt);

        // 审核通过 第三方接口推送
        if (InboundReceiptEnum.InboundReceiptStatus.REVIEW_PASSED.getValue().equals(inboundReceiptReviewDTO.getStatus())) {
            InboundReceiptInfoVO inboundReceiptInfoVO = this.queryInfo(warehouseNo, false);
            remoteRequest.createInboundReceipt(inboundReceiptInfoVO);
        }

    }


    /**
     * 异步保存附件
     * 空对象不会调用远程接口，空数组会删除所对应的附件
     * @param warehouseNo
     * @param inboundReceiptDTO
     */
    private void asyncAttachment(String warehouseNo, InboundReceiptDTO inboundReceiptDTO) {
        CompletableFuture.runAsync(() -> {
            List<AttachmentFileDTO> documentsFile = inboundReceiptDTO.getDocumentsFile();
            if (CollectionUtils.isNotEmpty(documentsFile)) {
                log.info("保存单证信息文件：{}", documentsFile);
                remoteComponent.saveAttachment(warehouseNo, documentsFile, AttachmentTypeEnum.INBOUND_RECEIPT_DOCUMENTS);
            }
        });
    }

}

