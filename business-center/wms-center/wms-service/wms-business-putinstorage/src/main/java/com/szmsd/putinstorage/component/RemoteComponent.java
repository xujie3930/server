package com.szmsd.putinstorage.component;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.BasCodeDto;
import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.bas.api.domain.dto.AttachmentDTO;
import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.bas.api.feign.BasWarehouseFeignService;
import com.szmsd.bas.api.feign.BaseProductFeignService;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductBatchQueryDto;
import com.szmsd.bas.dto.BaseProductMeasureDto;
import com.szmsd.chargerules.api.feign.OperationFeignService;
import com.szmsd.chargerules.enums.DelOutboundOrderEnum;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.language.enums.LocalLanguageEnum;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.vo.DelOutboundOperationDetailVO;
import com.szmsd.delivery.vo.DelOutboundOperationVO;
import com.szmsd.http.api.feign.HtpInboundFeignService;
import com.szmsd.http.dto.CreateTrackRequest;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.inventory.api.feign.InventoryFeignService;
import com.szmsd.inventory.domain.dto.InboundInventoryDTO;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;
import com.szmsd.putinstorage.domain.dto.ReceivingRequest;
import com.szmsd.putinstorage.domain.vo.InboundReceiptDetailVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.api.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 远程接口
 *
 * @author liangchao
 * @date 2020/12/21
 */
@Component
@Slf4j
public class RemoteComponent {

    @Resource
    private RemoteUserService remoteUserService;

    /**
     * 单号生成
     **/
    @Resource
    private BasFeignService basFeignService;

    /**
     * 附件远程服务
     **/
    @Resource
    private RemoteAttachmentService remoteAttachmentService;

    @Resource
    private BaseProductFeignService baseProductFeignService;

    /**
     * 库存远程服务
     **/
    @Resource
    private InventoryFeignService inventoryFeignService;

    /**
     * 仓库信息
     **/
    @Resource
    private BasWarehouseFeignService basWarehouseFeignService;

    @Resource
    private OperationFeignService operationFeignService;

    @Resource
    private HtpInboundFeignService htpInboundFeignService;

    /**
     * 获取登录人信息
     *
     * @return
     */
    public SysUser getLoginUserInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        AssertUtil.notNull(loginUser, "登录过期, 请重新登录");
        SysUser sysUser = remoteUserService.queryGetInfoByUserId(loginUser.getUserId()).getData();
        return sysUser;
    }

    /**
     * 单号生成
     *
     * @param code
     * @return
     */
    public String genNo(String code) {
        log.info("调用自动生成单号：code={}", code);
        R<List<String>> r = basFeignService.create(new BasCodeDto().setAppId("ck1").setCode(code));
        AssertUtil.notNull(r, "单号生成失败");
        AssertUtil.isTrue(r.getCode() == HttpStatus.SUCCESS, code + "单号生成失败：" + r.getMsg());
        String s = r.getData().get(0);
        log.info("调用自动生成单号：调用完成, {}-{}", code, s);
        return s;
    }

    /**
     * 获取入库单号 客户代码+年月日+6位流水
     *
     * @param cusCode
     * @return
     */
    public String getWarehouseNo(String cusCode) {
        AssertUtil.isTrue(StringUtils.isNotEmpty(cusCode), "客户代码不能为空");
        String warehouseNo = this.genNo("INBOUND_RECEIPT_NO");
        String substring = warehouseNo.substring(2);

        // 获取前缀
        R<List<BasSub>> getsub = basFeignService.getsub(new BasSub().setSubCode("067001"));
        if (getsub != null && CollectionUtils.isNotEmpty(getsub.getData())) {
            String subValue = getsub.getData().get(0).getSubValue();
            if (StringUtils.isNotEmpty(subValue)) {
                return subValue.concat(cusCode).concat(substring);
            }
        }
        return cusCode.concat(substring);
    }

    /**
     * 查询附件
     *
     * @param queryDTO
     * @return
     */
    public List<BasAttachment> getAttachment(BasAttachmentQueryDTO queryDTO) {
        List<BasAttachment> data = ListUtils.emptyIfNull(remoteAttachmentService.list(queryDTO).getData());
        return data;
    }

    /**
     * 保存或删除附件
     *
     * @param businessNo         业务编号
     * @param fileList           附件
     * @param attachmentTypeEnum 附件类型枚举
     */
    public void saveAttachment(String businessNo, List<AttachmentFileDTO> fileList, AttachmentTypeEnum attachmentTypeEnum) {
        saveAttachment(businessNo, null, fileList, attachmentTypeEnum);
    }

    /**
     * 保存或删除附件
     *
     * @param businessNo         业务编号
     * @param businessItemNo     业务明细号
     * @param fileList           附件
     * @param attachmentTypeEnum 附件类型枚举
     */
    public void saveAttachment(String businessNo, String businessItemNo, List<AttachmentFileDTO> fileList, AttachmentTypeEnum attachmentTypeEnum) {
        List<AttachmentDataDTO> attachmentDataDTOS = BeanMapperUtil.mapList(fileList, AttachmentDataDTO.class);
        remoteAttachmentService.saveAndUpdate(AttachmentDTO.builder().businessNo(businessNo).businessItemNo(businessItemNo).fileList(attachmentDataDTOS).attachmentTypeEnum(attachmentTypeEnum).build());
    }

    /**
     * 验证sku，验证失抛异常
     *
     * @param sku
     * @return
     */
    public void vailSku(String sku) {
        log.info("SKU验证：SKU={}", sku);
        if (CheckTag.get()) {
            log.info("|转运入库单不校验sku");
            return;
        }
        R<Boolean> booleanR = baseProductFeignService.checkSkuValidToDelivery(new BaseProduct().setCode(sku));
        AssertUtil.notNull(booleanR, "SKU验证失败");
        AssertUtil.isTrue(booleanR.getData() != null && booleanR.getData(), "SKU验证失败：" + booleanR.getMsg());
    }

    /**
     * 库存 入库
     *
     * @param receivingRequest
     */
    public void inboundInventory(ReceivingRequest receivingRequest) {
        log.info("调用入库接口：{}", receivingRequest);
        InboundInventoryDTO inboundInventoryDTO = BeanMapperUtil.map(receivingRequest, InboundInventoryDTO.class);
        R inbound = inventoryFeignService.inbound(inboundInventoryDTO);
        AssertUtil.notNull(inbound, "入库请求异常");
        AssertUtil.isTrue(inbound.getCode() == HttpStatus.SUCCESS, "入库失败：" + inbound.getMsg());
        log.info("调用入库接口：操作完成");
    }

    /**
     * 获取仓库信息
     *
     * @param warehouseCode
     * @return
     */
    public BasWarehouse getWarehouse(String warehouseCode) {
        R<BasWarehouse> basWarehouseR = basWarehouseFeignService.queryByWarehouseCode(warehouseCode);
        BasWarehouse warehouse = basWarehouseR.getData();
        AssertUtil.notNull(warehouse, "仓库信息获取失败：warehouseCode=" + warehouseCode);
        log.info("远程接口：查询SKU, warehouseCode={}, {}", warehouseCode, warehouse);
        return basWarehouseR.getData();
    }


    /**
     * 判断入库单是否自动审核
     *
     * @param warehouseCode
     * @return
     */
    public boolean inboundReceiptReview(String warehouseCode) {
        try {
            BasWarehouse warehouse = getWarehouse(warehouseCode);
            String inboundReceiptReview = warehouse.getInboundReceiptReview();
            LocalLanguageEnum localLanguageEnum = LocalLanguageEnum.getLocalLanguageEnum(LocalLanguageTypeEnum.INBOUND_RECEIPT_REVIEW, inboundReceiptReview);
            LocalLanguageEnum inboundReceiptReview0 = LocalLanguageEnum.INBOUND_RECEIPT_REVIEW_0;
            log.info(warehouse.getWarehouseNameCn() + "{}", ObjectUtils.isEmpty(localLanguageEnum) ? inboundReceiptReview0.getZhName() : localLanguageEnum.getZhName());
            return inboundReceiptReview0.getKey().equals(inboundReceiptReview);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * 删除附件
     *
     * @param attachmentTypeEnum
     * @param businessNo
     * @param businessItemNo
     */
    public void deleteAttachment(AttachmentTypeEnum attachmentTypeEnum, String businessNo, String businessItemNo) {
        remoteAttachmentService.deleteByBusinessNo(AttachmentDTO.builder().attachmentTypeEnum(attachmentTypeEnum).businessNo(businessNo).businessItemNo(businessItemNo).build());
    }

    /**
     * 批量查询sku
     *
     * @param sku
     * @return
     */
    public List<BaseProductMeasureDto> querySku(List<String> sku, String cusCode) {
        BaseProductBatchQueryDto queryDto = new BaseProductBatchQueryDto();
        queryDto.setCodes(sku);
        queryDto.setSellerCode(cusCode);
        R<List<BaseProductMeasureDto>> listR = baseProductFeignService.batchSKU(queryDto);
        if (listR == null) {
            return ListUtil.empty();
        }
        List<BaseProductMeasureDto> data = listR.getData();
        if (data == null) {
            return ListUtil.empty();
        }
        return data;
    }

    /**
     * 获取SKU信息
     *
     * @param code
     * @return
     */
    public BaseProduct getSku(String code, String sellerCode) {
        R<BaseProduct> result = baseProductFeignService.getSku(new BaseProduct().setCode(code).setSellerCode(sellerCode));
        BaseProduct sku = Optional.ofNullable(result.getData()).orElseGet(BaseProduct::new);
        log.info("远程接口：查询SKU, code={}, {}", code, sku);
        return sku;
    }

    /**
     * 入库冻结余额
     */
    public void delOutboundCharge(InboundReceiptInfoVO inboundReceiptInfoVO) {
        DelOutboundOperationVO delOutboundOperationVO = new DelOutboundOperationVO();
        delOutboundOperationVO.setOrderNo(inboundReceiptInfoVO.getWarehouseNo());
        delOutboundOperationVO.setOrderType(DelOutboundOrderEnum.FREEZE_IN_STORAGE.getCode());
        delOutboundOperationVO.setWarehouseCode(inboundReceiptInfoVO.getWarehouseCode());
        delOutboundOperationVO.setCustomCode(inboundReceiptInfoVO.getCusCode());
        delOutboundOperationVO.setWeight(null);

        List<InboundReceiptDetailVO> inboundReceiptDetailList = Optional.ofNullable(inboundReceiptInfoVO.getInboundReceiptDetails()).orElse(new ArrayList<>());
        List<DelOutboundOperationDetailVO> details = inboundReceiptDetailList.stream().map(x -> {
            DelOutboundOperationDetailVO delOutboundOperationDetailVO = new DelOutboundOperationDetailVO();
            delOutboundOperationDetailVO.setWeight(0.00);
            //使用申报数量
            delOutboundOperationDetailVO.setQty((long) x.getDeclareQty());
            delOutboundOperationDetailVO.setSku(x.getSku());
            return delOutboundOperationDetailVO;
        }).collect(Collectors.toList());
        delOutboundOperationVO.setDetails(details);
        log.info("调用冻结余额 {}", JSONObject.toJSONString(delOutboundOperationVO));
        R r = operationFeignService.delOutboundFreeze(delOutboundOperationVO);
        R.getDataAndException(r);
    }

    /**
     * 创建入库单物流信息列表
     */
    public void createTracking(CreateInboundReceiptDTO createInboundReceiptDTO) {
        log.info("创建入库单： {}", createInboundReceiptDTO);
        if (CollectionUtils.isEmpty(createInboundReceiptDTO.getInboundReceiptDetails())) return;
        CreateTrackRequest createTrackRequest = new CreateTrackRequest();
        createTrackRequest.setWarehouseCode(createInboundReceiptDTO.getWarehouseCode())
                .setRefOrderNo(createInboundReceiptDTO.getOrderNo())
                .setTrackingNumberList(createInboundReceiptDTO.getDeliveryNoList());
        log.info("创建入库单物流信息列表 {}", createTrackRequest);
        R<ResponseVO> tracking = htpInboundFeignService.createTracking(createTrackRequest);
        AssertUtil.isTrue(tracking.getCode() == HttpStatus.SUCCESS, tracking.getMsg());

    }
}
