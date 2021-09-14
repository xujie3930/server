package com.szmsd.doc.api.sku.request;

import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.api.feign.BasePackingFeignService;
import com.szmsd.bas.dto.BasePackingDto;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.doc.component.IRemoterApi;
import com.szmsd.doc.config.DocSubConfigData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Data
public class ProductRequest extends BaseProductRequest {
    @ApiModelProperty(value = "产品图片Base64", example = "xxx")
    private String productImageBase64;

    @ApiModelProperty(value = "文件信息")
    private List<AttachmentDataDTO> documentsFiles;

    public ProductRequest validData(DocSubConfigData docSubConfigData) {

        // 1、如果产品属性是带电的，带电信息和电池包装必填；
        if (StringUtils.isNotBlank(super.getProductAttribute())) {
         /*   DocSubConfigData.SubCode subCode = docSubConfigData.getSubCode();
            String charged = subCode.getCharged();*/
            if ("Battery".equals(super.getProductAttribute())) {
                AssertUtil.isTrue(StringUtils.isNotBlank(super.getElectrifiedMode()), "产品属性【带电】，带电信息不能为空");
                AssertUtil.isTrue(StringUtils.isNotBlank(super.getElectrifiedModeName()), "产品属性【带电】，带电信息不能为空");

                AssertUtil.isTrue(StringUtils.isNotBlank(super.getBatteryPackaging()), "产品属性【带电】，电池包装不能为空");
                AssertUtil.isTrue(StringUtils.isNotBlank(super.getBatteryPackagingName()), "产品属性【带电】，电池包装不能为空");
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
            Double initLength = Optional.ofNullable(super.getInitLength()).orElse(1.00);
            Double initHeight = Optional.ofNullable(super.getInitHeight()).orElse(1.00);
            Double initWeight = Optional.ofNullable(super.getInitWeight()).orElse(1.00);
            double volume = initLength * initHeight * initWeight;
            BigDecimal bigDecimal = BigDecimal.valueOf(volume).setScale(2, RoundingMode.HALF_UP);
            super.setInitVolume(bigDecimal);
        }
        return this;
    }

    public ProductRequest checkPack(BasePackingFeignService basePackingFeignService) {
        // 3、选择物物流包装OMS要校验是否存在；
        String suggestPackingMaterial = super.getSuggestPackingMaterial();
        if (StringUtils.isNotBlank(suggestPackingMaterial)) {
            R<List<BasePackingDto>> basePackingDtoR = basePackingFeignService.listParent();
            List<BasePackingDto> dataAndException = R.getDataAndException(basePackingDtoR);
            dataAndException.stream().filter(x -> suggestPackingMaterial.equals(x.getPackingMaterialType())).findAny()
                    .orElseThrow(() -> new RuntimeException("请检查物流包装是否存在!"));
        }
        return this;
    }

    public ProductRequest setTheCode(IRemoterApi iRemoterApi ,DocSubConfigData docSubConfigData) {
        DocSubConfigData.MainSubCode mainSubCode = docSubConfigData.getMainSubCode();
//        iRemoterApi.getSubNameByCode()
        return this;
    }
}
