package com.szmsd.putinstorage.component;

import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.BasCodeDto;
import com.szmsd.bas.api.domain.dto.AttachmentDTO;
import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.bas.api.feign.BaseProductFeignService;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.inventory.api.feign.InventoryFeignService;
import com.szmsd.inventory.domain.dto.ReceivingRequest;
import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.api.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

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

    /** 单号生成 **/
    @Resource
    private BasFeignService basFeignService;

    /** 附件远程服务 **/
    @Resource
    private RemoteAttachmentService remoteAttachmentService;

    @Resource
    private BaseProductFeignService baseProductFeignService;

    /** 库存远程服务 **/
    @Resource
    private InventoryFeignService inventoryFeignService;

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
     * @param code
     * @return
     */
    public String genNo(String code) {
        log.info("调用自动生成单号：code={}", code);
        R<List<String>> r = basFeignService.create(new BasCodeDto().setAppId("ck1").setCode(code));
        AssertUtil.isTrue(r != null && r.getCode() == HttpStatus.SUCCESS, code + "单号生成失败：" + r.getMsg());
        String s = r.getData().get(0);
        log.info("调用自动生成单号：调用完成, {}-{}", code, s);
        return s;
    }

    /**
     * 获取入库单号 客户代码+年月日+6位流水
     * @param cusCode
     * @return
     */
    public String getWarehouseNo(String cusCode) {
        String warehouseNo = this.genNo("INBOUND_RECEIPT_NO");
        String substring = warehouseNo.substring(2);
        return cusCode.concat(substring);
    }

    /**
     * 查询附件
     * @param queryDTO
     * @return
     */
    public List<BasAttachment> getAttachment(BasAttachmentQueryDTO queryDTO) {
        List<BasAttachment> data = ListUtils.emptyIfNull(remoteAttachmentService.list(queryDTO).getData());
        return data;
    }

    /**
     * 保存或删除附件
     * @param businessNo 业务编号
     * @param fileList 附件
     * @param attachmentTypeEnum 附件类型枚举
     */
    public void saveAttachment(String businessNo, List<AttachmentDataDTO> fileList, AttachmentTypeEnum attachmentTypeEnum) {
        saveAttachment(businessNo, null, fileList, attachmentTypeEnum);
    }

    /**
     * 保存或删除附件
     * @param businessNo 业务编号
     * @param businessItemNo 业务明细号
     * @param fileList 附件
     * @param attachmentTypeEnum 附件类型枚举
     */
    public void saveAttachment(String businessNo, String businessItemNo, List<AttachmentDataDTO> fileList, AttachmentTypeEnum attachmentTypeEnum) {
        remoteAttachmentService.saveAndUpdate(AttachmentDTO.builder().businessNo(businessNo).businessItemNo(businessItemNo).fileList(fileList).attachmentTypeEnum(attachmentTypeEnum).build());
    }

    /**
     * 验证sku，验证失抛异常
     * @param sku
     * @return
     */
    public void vailSku(String sku) {
        log.info("验证SKU：SKU={}", sku);
        R<Boolean> booleanR = baseProductFeignService.checkSkuValidToDelivery(new BaseProduct().setCode(sku));
        AssertUtil.isTrue(booleanR.getData(), "SKU验证失败：" + booleanR.getMsg());
    }

    /**
     * 库存 上架入库
     * @param receivingRequest
     */
    public void inboundInventory(ReceivingRequest receivingRequest) {
        log.info("调用库存上架入库接口：{}", receivingRequest);
        R inbound = inventoryFeignService.inbound(receivingRequest);
        AssertUtil.isTrue(inbound != null && inbound.getCode() == HttpStatus.SUCCESS, inbound.getMsg());
        log.info("调用库存上架入库接口：操作完成");
    }

}
