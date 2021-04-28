package com.szmsd.putinstorage.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.language.enums.LocalLanguageEnum;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.ObjectMapperUtils;
import com.szmsd.putinstorage.component.CheckTag;
import com.szmsd.putinstorage.component.RemoteComponent;
import com.szmsd.putinstorage.component.RemoteRequest;
import com.szmsd.putinstorage.domain.InboundReceipt;
import com.szmsd.putinstorage.domain.dto.*;
import com.szmsd.putinstorage.domain.vo.*;
import com.szmsd.putinstorage.enums.InboundReceiptEnum;
import com.szmsd.putinstorage.mapper.InboundReceiptMapper;
import com.szmsd.putinstorage.service.IInboundReceiptDetailService;
import com.szmsd.putinstorage.service.IInboundReceiptService;
import com.szmsd.putinstorage.util.ExcelUtil;
import com.szmsd.system.api.domain.SysUser;
import javafx.scene.control.CheckBox;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class InboundReceiptServiceImpl extends ServiceImpl<InboundReceiptMapper, InboundReceipt> implements IInboundReceiptService {

    @Value("${file.mainUploadFolder}")
    private String mainUploadFolder;

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
        String warehouseNo = queryDTO.getWarehouseNo();
        if (StringUtils.isNotEmpty(warehouseNo)) {
            List<String> warehouseNoSplit = Arrays.asList(warehouseNo.split(","));
            List<String> warehouseNoList = ListUtils.emptyIfNull(queryDTO.getWarehouseNoList());
            queryDTO.setWarehouseNoList(Stream.of(warehouseNoSplit, warehouseNoList).flatMap(Collection::stream).distinct().collect(Collectors.toList()));
        }
        String orderNo = queryDTO.getOrderNo();
        if (StringUtils.isNotEmpty(orderNo)) {
            List<String> orderNoSplit = Arrays.asList(orderNo.split(","));
            List<String> orderNoList = ListUtils.emptyIfNull(queryDTO.getOrderNoList());
            queryDTO.setOrderNoList(Stream.of(orderNoSplit, orderNoList).flatMap(Collection::stream).distinct().collect(Collectors.toList()));
        }

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
    public InboundReceiptInfoVO saveOrUpdate(CreateInboundReceiptDTO createInboundReceiptDTO) {
        log.info("创建入库单：{}", createInboundReceiptDTO);
        CheckTag.set(createInboundReceiptDTO.getIsFromTransport());
        Integer totalDeclareQty = createInboundReceiptDTO.getTotalDeclareQty();
        AssertUtil.isTrue(totalDeclareQty > 0, "合计申报数量不能为" + totalDeclareQty);

        // 保存入库单
        InboundReceipt inboundReceipt = this.saveOrUpdate((InboundReceiptDTO) createInboundReceiptDTO);
        String warehouseNo = inboundReceipt.getWarehouseNo();
        createInboundReceiptDTO.setWarehouseNo(warehouseNo);

        // 保存入库单明细
        List<InboundReceiptDetailDTO> inboundReceiptDetailDTOS = createInboundReceiptDTO.getInboundReceiptDetails();
        inboundReceiptDetailDTOS.forEach(item -> item.setWarehouseNo(warehouseNo));
        iInboundReceiptDetailService.saveOrUpdate(inboundReceiptDetailDTOS, createInboundReceiptDTO.getReceiptDetailIds());

        // 判断自动审核
        boolean inboundReceiptReview = remoteComponent.inboundReceiptReview(createInboundReceiptDTO.getWarehouseCode());
        if (inboundReceiptReview) {
            // 审核
            String localLanguage = LocalLanguageEnum.getLocalLanguageSplice(LocalLanguageEnum.INBOUND_RECEIPT_REVIEW_0);
            this.review(new InboundReceiptReviewDTO().setWarehouseNos(Arrays.asList(warehouseNo)).setStatus(InboundReceiptEnum.InboundReceiptStatus.REVIEW_PASSED.getValue()).setReviewRemark(localLanguage));

            // 第三方接口推送
            InboundReceiptInfoVO inboundReceiptInfoVO = this.queryInfo(warehouseNo, false);
            remoteRequest.createInboundReceipt(inboundReceiptInfoVO);
        }

        log.info("创建入库单：操作完成");
        return this.queryInfo(warehouseNo, false);
    }

    /**
     * 保存入库单信息
     * @param inboundReceiptDTO
     * @return
     */
    @Override
    public InboundReceipt saveOrUpdate(InboundReceiptDTO inboundReceiptDTO) {
        log.info("保存入库单：{}", inboundReceiptDTO);

        InboundReceipt inboundReceipt = BeanMapperUtil.map(inboundReceiptDTO, InboundReceipt.class);
        // 获取入库单号
        String warehouseNo = inboundReceipt.getWarehouseNo();
        if (StringUtils.isEmpty(warehouseNo)) {
            warehouseNo = remoteComponent.getWarehouseNo(inboundReceiptDTO.getCusCode());
        }
        inboundReceipt.setWarehouseNo(warehouseNo);
        this.saveOrUpdate(inboundReceipt);

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
        inboundReceiptInfoVO.setInboundReceiptDetails(inboundReceiptDetailVOS);
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
        InboundReceiptEnum.InboundReceiptEnumMethods anEnum = InboundReceiptEnum.InboundReceiptEnumMethods.getEnum(InboundReceiptEnum.InboundReceiptStatus.class, inboundReceiptReviewDTO.getStatus());
        inboundReceipt.setStatus(anEnum.getValue());
        inboundReceipt.setReviewRemark(inboundReceiptReviewDTO.getReviewRemark());
        inboundReceipt.setReviewBy(loginUserInfo.getUserId() + "");
        inboundReceipt.setReviewBy(loginUserInfo.getUserName());
        inboundReceipt.setReviewTime(new Date());
        List<String> warehouseNos = inboundReceiptReviewDTO.getWarehouseNos();
        log.info("入库单审核: {},{},{}", anEnum.getValue2(), warehouseNos, inboundReceipt);
        StringBuffer sb = new StringBuffer();
        warehouseNos.forEach(warehouseNo -> {
            inboundReceipt.setWarehouseNo(warehouseNo);
            // 审核通过 第三方接口推送
            if (!InboundReceiptEnum.InboundReceiptStatus.REVIEW_PASSED.getValue().equals(inboundReceiptReviewDTO.getStatus())) {
                return;
            }
            InboundReceiptInfoVO inboundReceiptInfoVO = this.queryInfo(warehouseNo, false);
            try {
                remoteRequest.createInboundReceipt(inboundReceiptInfoVO);
                this.updateByWarehouseNo(inboundReceipt);
            } catch (Exception e) {
                log.error(e.getMessage());
                sb.append(e.getMessage().replace("运行时异常", warehouseNo));
//                this.updateByWarehouseNo(new InboundReceipt().setWarehouseNo(warehouseNo).setStatus(InboundReceiptEnum.InboundReceiptStatus.REVIEW_FAILURE.getValue()).setReviewRemark(e.getMessage()));
            }
        });
        AssertUtil.isTrue(sb.length() == 0, () -> sb.toString());
    }

    /**
     * 删除入库单 物理删除
     * @param warehouseNo
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(String warehouseNo) {
        log.info("删除入库单, warehouseNo={}", warehouseNo);
        InboundReceiptVO inboundReceiptVO = this.selectByWarehouseNo(warehouseNo);
        AssertUtil.notNull(inboundReceiptVO, "入库单[" + warehouseNo + "]不存在");
        List<String> statues = Arrays.asList(InboundReceiptEnum.InboundReceiptStatus.INIT.getValue(), InboundReceiptEnum.InboundReceiptStatus.REVIEW_FAILURE.getValue());
        String status = inboundReceiptVO.getStatus();
        String statusName = InboundReceiptEnum.InboundReceiptEnumMethods.getValue2(InboundReceiptEnum.InboundReceiptStatus.class, status);
        AssertUtil.isTrue(statues.contains(status), "入库单[" + warehouseNo + "]" + statusName + "不能删除");
        baseMapper.deleteById(inboundReceiptVO.getId());

        // 删除明细
        iInboundReceiptDetailService.deleteAndFileByWarehouseNo(warehouseNo);

        // 删除附件
        asyncDeleteAttachment(warehouseNo);
    }

    /**
     * 入库单导出数据查询
     * @param queryDTO
     */
    @Override
    public List<InboundReceiptExportVO> selectExport(InboundReceiptQueryDTO queryDTO) {
        if (StringUtils.isNotEmpty(queryDTO.getWarehouseNo())) {
            List<String> warehouseNoSplit = Arrays.asList(queryDTO.getWarehouseNo().split(","));
            List<String> warehouseNoList = ListUtils.emptyIfNull(queryDTO.getWarehouseNoList());
            queryDTO.setWarehouseNoList(Stream.of(warehouseNoSplit, warehouseNoList).flatMap(Collection::stream).distinct().collect(Collectors.toList()));
        }
        List<InboundReceiptExportVO> inboundReceiptExportVOS = baseMapper.selectExport(queryDTO);
        List<InboundReceiptExportVO> serialize = ObjectMapperUtils.serialize(inboundReceiptExportVOS);
        return BeanMapperUtil.mapList(serialize, InboundReceiptExportVO.class);
    }

    /**
     * 导出sku
     * @param excel
     * @param details
     */
    @Override
    public void exportSku(Workbook excel, List<InboundReceiptDetailVO> details) {
        // 创建sheet
        Sheet sheet = excel.createSheet();
        // 内容
        List<InboundReceiptSkuExcelVO> sheetList = new ArrayList<>();

        // 列名
        sheetList.add(new InboundReceiptSkuExcelVO().setColumn0("SKU").setColumn1("英文申报品名").setColumn2("申报数量").setColumn3("上架数量").setColumn4("原产品编码").setColumn5("对版图片").setColumn6("备注"));

        // 入库单明细SKU
        sheetList.addAll(details.stream().map(detail -> {
            InboundReceiptSkuExcelVO vo = new InboundReceiptSkuExcelVO();
            vo.setColumn0(detail.getSku());
            vo.setColumn1(detail.getSkuName());
            vo.setColumn2(detail.getDeclareQty() + "");
            vo.setColumn3(detail.getPutQty() + "");
            vo.setColumn4(detail.getOriginCode());
            vo.setColumn5(detail.getEditionImage() == null ? "" : detail.getEditionImage().getAttachmentUrl());
            vo.setColumn6(detail.getRemark());
            return vo;
        }).collect(Collectors.toList()));
        log.info("导出sku: {}", details);

        // 创建行
        for (int i = 0; i < sheetList.size(); i++) {
            Row row = sheet.createRow(i);
            InboundReceiptSkuExcelVO vo = sheetList.get(i);
            Class<? extends InboundReceiptSkuExcelVO> aClass = vo.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (int i1 = 0; i1 < declaredFields.length; i1++) {
                // 反射获取value
                String value;
                try {
                    Field declaredField = declaredFields[i1];
                    declaredField.setAccessible(true);
                    value = (String) declaredField.get(vo);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    value = "";
                }

                // 第2行 至 最后一行 第5列插入图片
                if ((i > 0) && (i1 == 5) && StringUtils.isNotEmpty(value) && !"null".equals(value)) {
                    String file = value;
                    try {
                        file = mainUploadFolder + File.separator + new URL(value).getFile();
                        ExcelUtil.insertImage(excel, sheet, i, i1, file);
                    } catch (Exception e) {
                        log.error("第{}行图片插入失败, imageUrl={}, {}", i, file, e.getMessage());
                    }
                    continue;
                }

                // 单元格赋值
                row.createCell(i1).setCellValue(value);
            }
            // 设置样式
            ExcelUtil.bord(excel, row, false, 6);
        }
    }

    /**
     * 异步删除附件
     * @param warehouseNo
     */
    private void asyncDeleteAttachment(String warehouseNo) {
        CompletableFuture.runAsync(() -> {
            AttachmentTypeEnum inboundReceiptDocuments = AttachmentTypeEnum.INBOUND_RECEIPT_DOCUMENTS;
            log.info("删除入库单[{}]{}", warehouseNo, inboundReceiptDocuments.getAttachmentType());
            remoteComponent.deleteAttachment(inboundReceiptDocuments, warehouseNo, null);
        });
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
            if (documentsFile != null) {
                log.info("保存单证信息文件：{}", documentsFile);
                remoteComponent.saveAttachment(warehouseNo, documentsFile, AttachmentTypeEnum.INBOUND_RECEIPT_DOCUMENTS);
            }
        });
    }

}

