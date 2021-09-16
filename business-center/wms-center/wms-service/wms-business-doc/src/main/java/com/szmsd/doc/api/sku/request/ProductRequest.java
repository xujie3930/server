package com.szmsd.doc.api.sku.request;

import cn.hutool.core.codec.Base64Encoder;
import com.sun.org.apache.xpath.internal.operations.Mult;
import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentDataDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.BasePackingFeignService;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.dto.BasePackingDto;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.doc.component.IRemoterApi;
import com.szmsd.doc.config.DocSubConfigData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Data
public class ProductRequest extends BaseProductRequest {

    @ApiModelProperty(value = "产品图片Base64", example = "xxx")
    private String productImageBase64;

    @ApiModelProperty(value = "文件信息",hidden = true)
    private List<AttachmentDataDTO> documentsFiles;

    public ProductRequest validData(DocSubConfigData docSubConfigData) {
        if (null == super.getHavePackingMaterial() || !super.getHavePackingMaterial()) {
            super.setBindCode(null);
            super.setBindCodeName(null);
        }
        // 1、如果产品属性是带电的，带电信息和电池包装必填；
        if (StringUtils.isNotBlank(super.getProductAttribute())) {
         /*   DocSubConfigData.SubCode subCode = docSubConfigData.getSubCode();
            String charged = subCode.getCharged();*/
            if ("Battery".equals(super.getProductAttribute())) {
                AssertUtil.isTrue(StringUtils.isNotBlank(super.getElectrifiedMode()), "产品属性【带电】，带电信息不能为空");
                AssertUtil.isTrue(StringUtils.isNotBlank(super.getElectrifiedModeName()), "产品属性【带电】，带电信息不能为空");

                AssertUtil.isTrue(StringUtils.isNotBlank(super.getBatteryPackaging()), "产品属性【带电】，电池包装不能为空");
                AssertUtil.isTrue(StringUtils.isNotBlank(super.getBatteryPackagingName()), "产品属性【带电】，电池包装不能为空");
            } else {
                super.setElectrifiedMode(null);
                super.setElectrifiedModeName(null);
                super.setBatteryPackaging(null);
                super.setBatteryPackagingName(null);
            }
        }
        // 2、是否附带包材=是，附带包材必填；
        Optional.ofNullable(super.getHavePackingMaterial()).filter(x -> x).ifPresent(x -> {
            AssertUtil.isTrue(StringUtils.isNotBlank(super.getBindCode()), "附带包材选项,需要选择附带包材");
            AssertUtil.isTrue(StringUtils.isNotBlank(super.getBindCodeName()), "附带包材选项,需要选择附带包材");
        });

        // 5、页面内容以外的字段，均不要在新增接口体现。
        return this;
    }

    public ProductRequest calculateTheVolume() {
        // 4、体积，接口自动计算 体积如果为空，系统计算出来 新建SKU的时候， 体积给他用长*宽*高
        if (null == super.getInitVolume() || super.getInitVolume().compareTo(BigDecimal.ZERO) <= 0) {
            Double initLength = Optional.ofNullable(super.getInitLength()).orElse(0.00);
            Double initHeight = Optional.ofNullable(super.getInitHeight()).orElse(0.00);
            Double initWeight = Optional.ofNullable(super.getInitWeight()).orElse(0.00);
            double volume = initLength * initHeight * initWeight;
            BigDecimal bigDecimal = BigDecimal.valueOf(volume).setScale(2, RoundingMode.HALF_UP);
            super.setInitVolume(bigDecimal);
        }
        return this;
    }

    public ProductRequest checkPack(BasePackingFeignService basePackingFeignService) {
        // 3、选择物物流包装OMS要校验是否存在；
        String suggestPackingMaterialCode = super.getSuggestPackingMaterialCode();
        if (StringUtils.isNotBlank(suggestPackingMaterialCode)) {
            R<List<BasePackingDto>> basePackingDtoR = basePackingFeignService.listParent();
            List<BasePackingDto> dataAndException = R.getDataAndException(basePackingDtoR);
            BasePackingDto basePackingDto = dataAndException.stream().filter(x -> suggestPackingMaterialCode.equals(x.getPackageMaterialCode())).findAny()
                    .orElseThrow(() -> new RuntimeException("请检查物流包装是否存在!"));
            String packageMaterialName = basePackingDto.getPackingMaterialType();
            super.setSuggestPackingMaterial(packageMaterialName);
        }
        return this;
    }

    public ProductRequest setTheCode(IRemoterApi iRemoterApi, DocSubConfigData docSubConfigData) {
        DocSubConfigData.MainSubCode mainSubCode = docSubConfigData.getMainSubCode();

        String productAttributeConfig = mainSubCode.getProductAttribute();
        Optional<BasSubWrapperVO> basSubWrapperOpt = Optional.ofNullable(iRemoterApi.getSubNameByCode(productAttributeConfig))
                .flatMap(x -> Optional.ofNullable(x.get(super.getProductAttribute())));
        String productAttributeName = basSubWrapperOpt.map(BasSubWrapperVO::getSubName).orElseThrow(() -> new RuntimeException("产品属性不存在"));
        String productAttribute = basSubWrapperOpt.map(BasSubWrapperVO::getSubValue).orElseThrow(() -> new RuntimeException("产品属性不存在"));
        super.setProductAttribute(productAttribute);
        super.setProductAttributeName(productAttributeName);

        String electrifiedMode = super.getElectrifiedMode();
        Optional.ofNullable(electrifiedMode).filter(StringUtils::isNotBlank).ifPresent(code -> {
            String electrifiedModeName = Optional.ofNullable(iRemoterApi.getSubNameByCode(mainSubCode.getElectrifiedMode()))
                    .map(map -> map.get(code)).map(BasSubWrapperVO::getSubName).orElseThrow(() -> new RuntimeException("电池包装不存在"));
            super.setElectrifiedModeName(electrifiedModeName);
        });

        return this;
    }

    public ProductRequest uploadFile(IRemoterApi remoterApi) {
        String productImageBase64 = this.getProductImageBase64();
        byte[] bytes = Base64Utils.decodeFromString(productImageBase64);

        RemoteAttachmentService remoteAttachmentService = remoterApi.getRemoteAttachmentService();

        MockMultipartFile byteArrayMultipartFile = new MockMultipartFile(super.getProductName(),"","jpg",bytes);
        MockMultipartFile[] mockMultipartFiles = {byteArrayMultipartFile};
        R<List<BasAttachmentDataDTO>> listR = remoteAttachmentService.uploadAttachment(mockMultipartFiles, AttachmentTypeEnum.SKU_IMAGE, null, null);
        List<BasAttachmentDataDTO> dataAndException = R.getDataAndException(listR);
        //只有一张图片
        Optional.ofNullable(dataAndException).filter(CollectionUtils::isNotEmpty).map(x->x.get(0)).map(BasAttachmentDataDTO::getAttachmentUrl).ifPresent(x->{super.setProductImage(x);
            AttachmentDataDTO attachmentDataDTO1 = new AttachmentDataDTO();
            attachmentDataDTO1.setAttachmentUrl(x);
            this.setDocumentsFiles(Collections.singletonList(attachmentDataDTO1));
        });
        return this;
    }


}
