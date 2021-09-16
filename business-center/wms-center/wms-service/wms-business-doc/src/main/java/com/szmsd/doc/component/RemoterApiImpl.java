package com.szmsd.doc.component;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;
import com.alibaba.fastjson.JSONObject;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentDataDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.doc.api.warehouse.req.FileInfoBase64;
import com.szmsd.doc.utils.AuthenticationUtil;
import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.api.feign.RemoteUserService;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: RemoterApiImpl
 * @Description:
 * @Author: 11
 * @Date: 2021-09-11 14:23
 */
@Slf4j
@Component
public class RemoterApiImpl implements IRemoterApi {

    @Autowired
    private BaseProductClientService baseProductClientService;
    @Resource
    private BasWarehouseClientService basWarehouseClientService;
    @Resource
    private BasSubClientService basSubClientService;
    @Resource
    private RemoteUserService remoteUserService;
    @Resource
    private RemoteAttachmentService remoteAttachmentService;

    @Override
    public RemoteAttachmentService getRemoteAttachmentService() {
        return this.remoteAttachmentService;
    }

    @Override
    public List<BasAttachmentDataDTO> uploadFile(List<FileInfoBase64> base64List, AttachmentTypeEnum attachmentTypeEnum) {
        if (CollectionUtils.isEmpty(base64List)) return new ArrayList<>();
        MockMultipartFile[] mockMultipartFiles = new MockMultipartFile[base64List.size()];
        for (int i = 0; i < base64List.size(); i++) {
            FileInfoBase64 fileInfoBase64 = base64List.get(i);
            byte[] bytes = Base64Utils.decodeFromString(fileInfoBase64.getFileBase64());
            MockMultipartFile byteArrayMultipartFile = new MockMultipartFile(fileInfoBase64.getFileName() + "." + fileInfoBase64.getSuffix(), fileInfoBase64.getFileName() + "." + fileInfoBase64.getSuffix(), fileInfoBase64.getSuffix(), bytes);
            mockMultipartFiles[i] = byteArrayMultipartFile;
        }
        R<List<BasAttachmentDataDTO>> listR = remoteAttachmentService.uploadAttachment(mockMultipartFiles, attachmentTypeEnum, null, null);
        return R.getDataAndException(listR);
    }

    @Override
    public void getUserInfo() {
        R info = remoteUserService.getInfo(1);
        Map data = (Map) info.getData();
        // todo 此处修改ajax.put返回  R.ok(map)
        Object userObj = data.get("user");
        String jsonString = JSONObject.toJSONString(userObj);
        SysUser user = JSONObject.parseObject(jsonString, SysUser.class);

//        map.put("user", userService.selectUserById(userId));//SysUser
//        SysUser
//        map.put("roles", roles);
//        map.put("permissions", permissions);
//        CurrentUserInfo.setSysUser(user);
    }

    @Override
    public boolean verifyWarehouse(String warehouse) {
        List<BasWarehouse> basWarehouses = basWarehouseClientService.queryByWarehouseCodes(Collections.singletonList(warehouse));
        if (CollectionUtils.isNotEmpty(basWarehouses)) return true;
        return false;
    }

    @Override
    public boolean checkSkuBelong(String sellerCode, String warehouse, String sku) {
        return this.checkSkuBelong(sellerCode, warehouse, Collections.singletonList(sku));
    }

    @Override
    public boolean checkSkuBelong(String sku) {
        return this.checkSkuBelong(AuthenticationUtil.getSellerCode(), null, Collections.singletonList(sku));
    }

    @Override
    public boolean checkSkuBelong(String sellerCode, String warehouse, List<String> sku) {
        BaseProductConditionQueryDto baseProductConditionQueryDto = new BaseProductConditionQueryDto();
        baseProductConditionQueryDto.setSkus(sku);
        baseProductConditionQueryDto.setSellerCode(sellerCode);
//        baseProductConditionQueryDto.setWarehouseCode(warehouse);
        List<BaseProduct> baseProducts = baseProductClientService.queryProductList(baseProductConditionQueryDto);
        if (CollectionUtils.isNotEmpty(baseProducts)) return true;
        return false;
    }

    //  mainCode, subCode, Info
    TimedCache<String, Map<String, BasSubWrapperVO>> timedCache = CacheUtil.newTimedCache(DateUnit.MINUTE.getMillis() * 30);

    @Override
    public Map<String, BasSubWrapperVO> getSubNameByCode(String mainCode) {
        Map<String, BasSubWrapperVO> subCodeWithInfo = timedCache.get(mainCode);
        if (subCodeWithInfo == null) {
            Map<String, List<BasSubWrapperVO>> sub = basSubClientService.getSub(mainCode);
            sub.forEach((x, y) -> {
                Map<String, BasSubWrapperVO> collect = y.stream().collect(Collectors.toMap(BasSubWrapperVO::getSubCode, subCodeInfo -> subCodeInfo, (x1, x2) -> x1));
                Map<String, BasSubWrapperVO> collect2 = y.stream().collect(Collectors.toMap(BasSubWrapperVO::getSubValue, subCodeInfo -> subCodeInfo, (x1, x2) -> x1));
                collect.putAll(collect2);
                timedCache.put(x, collect);
            });
            subCodeWithInfo = timedCache.get(mainCode);
        }
        return subCodeWithInfo;
    }

    @Override
    public boolean checkPackBelong(String bindCode) {
        String sellerCode = AuthenticationUtil.getSellerCode();
        BaseProduct queryDTO = new BaseProduct();
        queryDTO.setSellerCode(sellerCode);
        queryDTO.setCategory("包材");
        List<BaseProduct> baseProducts = baseProductClientService.listSku(queryDTO);
        return baseProducts.stream().map(BaseProduct::getCode).anyMatch(x -> x.equals(bindCode));
    }

    @Override
    public List<BaseProduct> listSku(BaseProduct baseProduct) {
        return baseProductClientService.listSku(baseProduct);
    }
}
