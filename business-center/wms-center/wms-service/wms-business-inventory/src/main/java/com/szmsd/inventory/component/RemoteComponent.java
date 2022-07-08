package com.szmsd.inventory.component;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.bas.api.feign.BaseProductFeignService;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BasSellerEmailDto;
import com.szmsd.bas.dto.BaseProductBatchQueryDto;
import com.szmsd.bas.dto.BaseProductMeasureDto;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.vo.DelOutboundDetailVO;
import com.szmsd.putinstorage.api.feign.InboundReceiptFeignService;
import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptQueryDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;
import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.api.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

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

    @Resource
    private BaseProductFeignService baseProductFeignService;

    @Resource
    private InboundReceiptFeignService inboundReceiptFeignService;

    @Resource
    private DelOutboundFeignService delOutboundFeignService;

    @Resource
    private BasSellerFeignService basSellerFeignService;

    /**
     * 获取登录人信息
     *
     * @return
     */
    public SysUser getLoginUserInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return new SysUser();
        }
        SysUser sysUser = remoteUserService.queryGetInfoByUserId(loginUser.getUserId()).getData();
        return Optional.ofNullable(sysUser).orElseGet(SysUser::new);
    }

    /**
     * 获取SKU信息
     *
     * @param code
     * @return
     */
    public BaseProduct getSku(String code) {
        R<BaseProduct> result = baseProductFeignService.getSku(new BaseProduct().setCode(code));
        BaseProduct sku = Optional.ofNullable(result.getData()).orElseGet(BaseProduct::new);
        log.info("远程接口：查询SKU, code={}, {}", code, sku);
        return sku;
    }

    /**
     * 批量获取sku信息
     *
     * @param codes
     * @return
     */
    public List<BaseProductMeasureDto> listSku(List<String> codes) {
        BaseProductBatchQueryDto queryDto = new BaseProductBatchQueryDto();
        queryDto.setCodes(codes);
        R<List<BaseProductMeasureDto>> result = baseProductFeignService.batchSKU(queryDto);
        List<BaseProductMeasureDto> skuList = ListUtils.emptyIfNull(result.getData());
        log.info("远程接口：查询SKU, code={}, {}", codes, skuList);
        return skuList;
    }

    /**
     * 采购单 [入库 - 新增/创建]
     */
    public InboundReceiptInfoVO orderStorage(CreateInboundReceiptDTO createInboundReceiptDTO) {
        createInboundReceiptDTO.setIsAsync(true);
        log.info("入库开始 请求参数 {}", JSONObject.toJSONString(createInboundReceiptDTO));
        R<InboundReceiptInfoVO> inboundReceiptInfoVO = inboundReceiptFeignService.saveOrUpdate(createInboundReceiptDTO);
        log.info("入库完成 {}", JSONObject.toJSONString(inboundReceiptInfoVO));
        InboundReceiptInfoVO resultData = R.getDataAndException(inboundReceiptInfoVO);
        return resultData;
    }

    /**
     * 获取转运出库数据详情
     *
     * @param idList
     */
    public List<DelOutboundDetailVO> getTransshipmentProductData(List<String> idList) {
        log.info("获取转运出库数据 请求参数 {}", JSONObject.toJSONString(idList));
        R<List<DelOutboundDetailVO>> transshipmentProductData = delOutboundFeignService.getTransshipmentProductData(idList);
        List<DelOutboundDetailVO> dataAndException = R.getDataAndException(transshipmentProductData);
        log.info("获取转运出库数据完成 请求参数 {}", JSONObject.toJSONString(dataAndException));
        return dataAndException;
    }

    /**
     * 出库-创建采购单后回写出库单 采购单号
     * 多个出库单，对应一个采购单
     *
     * @param purchaseNo  采购单号
     * @param orderNoList 出库单列表
     * @return
     */
    public void setPurchaseNo(String purchaseNo, List<String> orderNoList) {
        log.info("出库-创建采购单后回写出库单采购单号{}-采购单号{}", JSONObject.toJSONString(orderNoList), purchaseNo);
        if (CollectionUtils.isEmpty(orderNoList)) return;
        delOutboundFeignService.setPurchaseNo(purchaseNo, orderNoList);
    }

    /**
     * 查询所有客户
     */
    public List<BasSellerEmailDto> queryCusAll() {
        R<List<BasSellerEmailDto>> listR = basSellerFeignService.queryAllSellerCodeAndEmail();
        return listR == null ? null : listR.getData();
    }
    public InboundReceiptInfoVO getReceiptInfo(String warehouseNo){
        R<InboundReceiptInfoVO> info = inboundReceiptFeignService.info(warehouseNo);
        AssertUtil.isTrue(info.getCode()== HttpStatus.SUCCESS,"查询详情异常");
        return info.getData();
    }
    /**
     * 获取入库信息详情
     * @param warehouseNo
     */
    public List<InboundReceiptVO> getReceiptInfoList(String purshOrderNo){
        InboundReceiptQueryDTO inboundReceiptQueryDTO = new InboundReceiptQueryDTO();
        inboundReceiptQueryDTO.setOrderNo(purshOrderNo);
        R<List<InboundReceiptVO>> info = inboundReceiptFeignService.list(inboundReceiptQueryDTO);
        AssertUtil.isTrue(info.getCode()== HttpStatus.SUCCESS,"查询详情异常");
        return info.getData();
    }
}
