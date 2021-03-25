package com.szmsd.chargerules.service.impl;

import com.szmsd.chargerules.dto.PricedSheetDTO;
import com.szmsd.chargerules.service.IPricedSheetService;
import com.szmsd.chargerules.vo.PackageLimitVO;
import com.szmsd.chargerules.vo.PricedProductSheetVO;
import com.szmsd.chargerules.vo.PricedSheetInfoVO;
import com.szmsd.chargerules.vo.PricedVolumeWeightVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.http.api.feign.HtpPricedProductFeignService;
import com.szmsd.http.api.feign.HtpPricedSheetFeignService;
import com.szmsd.http.dto.CreatePricedSheetCommand;
import com.szmsd.http.dto.Packing;
import com.szmsd.http.dto.UpdatePricedSheetCommand;
import com.szmsd.http.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PricedSheetServiceImpl implements IPricedSheetService {

    @Resource
    private HtpPricedProductFeignService htpPricedProductFeignService;

    @Resource
    private HtpPricedSheetFeignService htpPricedSheetFeignService;


    /**
     * 根据产品代码获取计价产品信息 - 获取报价信息
     * https://pricedproduct-internalapi-external.dsloco.com/api/products/{productCode}
     * @param productCode
     * @return
     */
    @Override
    public List<PricedProductSheetVO> sheets(String productCode) {
        R<PricedProductInfo> info = htpPricedProductFeignService.info(productCode);
        PricedProductInfo data = info.getData();
        List<PricedProductSheet> sheets = data.getSheets();
        List<PricedProductSheetVO> pricedProductSheetVOS = BeanMapperUtil.mapList(sheets, PricedProductSheetVO.class);
        return pricedProductSheetVOS;
    }

    /**
     * 根据报价表编号获取产品报价表信息
     * https://pricedproduct-internalapi-external.dsloco.com/api/sheets/{sheetCode}
     * @param sheetCode
     * @return
     */
    @Override
    public PricedSheetInfoVO info(String sheetCode) {
        R<PricedSheet> info = htpPricedSheetFeignService.info(sheetCode);
        PricedSheet data = info.getData();
        if (data == null) {
            return null;
        }
        PricedSheetInfoVO result = BeanMapperUtil.map(data, PricedSheetInfoVO.class);
        List<PricedVolumeWeightVO> volumeWeights = data.getVolumeWeights().stream().map(item -> {
            PricedVolumeWeightVO vo = new PricedVolumeWeightVO();
            vo.setVolumeWeightType(item.getVolumeWeightType());
            vo.setVolumeWeightStandards(item.getVolumeWeightStandards());
            vo.setVolumeWeightReduce(item.getVolumeWeightReduce());

            PackageLimit packageLimit = item.getPackageLimit();
            vo.setMinPhysicalWeight(packageLimit.getMinPhysicalWeight());
            vo.setMaxPhysicalWeight(packageLimit.getMaxPhysicalWeight());
            vo.setVolumeLong(packageLimit.getVolumeLong());
            vo.setVolume(packageLimit.getVolume());
            vo.setPerimeter(packageLimit.getPerimeter());

            Packing packingLimit = packageLimit.getPackingLimit();
            vo.setPackingLimitStr(packingLimit.getLength() + "*" + packingLimit.getWidth() + "*" + packingLimit.getHeight());
            return vo;
        }).collect(Collectors.toList());
        result.setVolumeWeights(volumeWeights);

        PackageLimitVO limitVo = result.getLimit();
        PackageLimit limit = data.getLimit();
        Packing minPackingLimit = limit.getMinPackingLimit();
        limitVo.setMinPackingLimitStr(minPackingLimit.getLength() + "*" + minPackingLimit.getWidth() + "*" + minPackingLimit.getHeight());
        Packing packingLimit = limit.getPackingLimit();
        limitVo.setPackingLimitStr(packingLimit.getLength() + "*" + packingLimit.getWidth() + "*" + packingLimit.getHeight());
        result.setLimit(limitVo);

        return result;
    }

    /**
     * 创建报价产品报价表详情信息
     * https://pricedproduct-internalapi-external.dsloco.com/api/sheets
     * @param pricedSheetDTO
     */
    @Override
    public void create(PricedSheetDTO pricedSheetDTO) {
        CreatePricedSheetCommand create = BeanMapperUtil.map(pricedSheetDTO, CreatePricedSheetCommand.class);
        refactor(pricedSheetDTO, create);
        R<ResponseVO> responseVOR = htpPricedSheetFeignService.create(create);
        ResponseVO.resultAssert(responseVOR, "创建报价产品报价表详情信息");
    }

    /**
     * 修改报价产品报价表详情信息
     * https://pricedproduct-internalapi-external.dsloco.com/api/sheets/{sheetCode}
     * @param pricedSheetDTO
     */
    @Override
    public void update(PricedSheetDTO pricedSheetDTO) {
        UpdatePricedSheetCommand update = BeanMapperUtil.map(pricedSheetDTO, UpdatePricedSheetCommand.class);
        refactor(pricedSheetDTO, update);
        R<ResponseVO> responseVOR = htpPricedSheetFeignService.update(update);
        ResponseVO.resultAssert(responseVOR, "修改报价产品报价表详情信息");
    }

    private static <T> void refactor(PricedSheetDTO pricedSheetDTO, T t) {
        List<PricedVolumeWeight> volumeWeights = pricedSheetDTO.getVolumeWeights().stream().map(item -> {
            PricedVolumeWeight vw = new PricedVolumeWeight();
            vw.setVolumeWeightType(item.getVolumeWeightType());
            vw.setVolumeWeightStandards(item.getVolumeWeightStandards());
            vw.setVolumeWeightReduce(item.getVolumeWeightReduce());

            PackageLimit packageLimit = new PackageLimit();
            packageLimit.setMinPhysicalWeight(item.getMinPhysicalWeight());
            packageLimit.setMaxPhysicalWeight(item.getMaxPhysicalWeight());

            String packingLimit = StringUtils.defaultString(item.getPackingLimitStr());
            String[] split = packingLimit.split("\\*");
            AssertUtil.isTrue(split.length == 3, "包裹总尺寸填写不符合规则(L*W*H)");
            Packing packing = new Packing().setLength(new BigDecimal(split[0])).setWidth(new BigDecimal(split[1])).setHeight(new BigDecimal(split[2])).setLengthUnit("CM");
            packageLimit.setPackingLimit(packing);
            packageLimit.setVolumeLong(item.getVolumeLong());
            packageLimit.setVolume(item.getVolume());
            packageLimit.setPerimeter(item.getPerimeter());
            vw.setPackageLimit(packageLimit);

            return vw;
        }).collect(Collectors.toList());

        PackageLimitVO limitVo = pricedSheetDTO.getLimit();
        PackageLimit limit = BeanMapperUtil.map(limitVo, PackageLimit.class);

        String minPackingLimit = StringUtils.defaultString(limitVo.getMinPackingLimitStr());
        String[] split = minPackingLimit.split("\\*");
        AssertUtil.isTrue(split.length == 3, "最小尺寸填写不符合规则(L*W*H)");
        Packing minPacking = new Packing().setLength(new BigDecimal(split[0])).setWidth(new BigDecimal(split[1])).setHeight(new BigDecimal(split[2])).setLengthUnit("CM");
        limit.setMinPackingLimit(minPacking);

        String packingLimit = StringUtils.defaultString(limitVo.getPackingLimitStr());
        String[] split2 = packingLimit.split("\\*");
        AssertUtil.isTrue(split2.length == 3, "最大尺寸填写不符合规则(L*W*H)");
        Packing packing = new Packing().setLength(new BigDecimal(split2[0])).setWidth(new BigDecimal(split2[1])).setHeight(new BigDecimal(split2[2])).setLengthUnit("CM");
        limit.setPackingLimit(packing);

        if (t instanceof CreatePricedSheetCommand) {
            CreatePricedSheetCommand create = (CreatePricedSheetCommand) t;
            create.setVolumeWeights(volumeWeights);
            create.setLimit(limit);
        } else {
            UpdatePricedSheetCommand update = (UpdatePricedSheetCommand) t;
            update.setVolumeWeights(volumeWeights);
            update.setLimit(limit);
        }
    }

}
