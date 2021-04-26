package com.szmsd.chargerules.service.impl;

import com.szmsd.chargerules.dto.PricedSheetDTO;
import com.szmsd.chargerules.dto.ProductSheetGradeDTO;
import com.szmsd.chargerules.service.IPricedSheetService;
import com.szmsd.chargerules.vo.*;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.http.api.feign.HtpPricedProductFeignService;
import com.szmsd.http.api.feign.HtpPricedSheetFeignService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
        AssertUtil.notNull(data, info.getMsg());
        List<PricedProductSheet> sheets = data.getSheets();
        List<PricedProductSheetVO> pricedProductSheetVOS = BeanMapperUtil.mapList(sheets, PricedProductSheetVO.class);
        pricedProductSheetVOS.forEach(sheet -> sheet.setProductCode(productCode));
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
        List<PricedVolumeWeightVO> volumeWeights = data.getVolumeWeights() == null ? null : data.getVolumeWeights().stream().map(item -> {
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
        if (limit != null) {
            Packing minPackingLimit = limit.getMinPackingLimit();
            if (minPackingLimit != null) {
                limitVo.setMinPackingLimitStr(minPackingLimit.getLength() + "*" + minPackingLimit.getWidth() + "*" + minPackingLimit.getHeight());
            }
            Packing packingLimit = limit.getPackingLimit();
            if (packingLimit != null) {
                limitVo.setPackingLimitStr(packingLimit.getLength() + "*" + packingLimit.getWidth() + "*" + packingLimit.getHeight());
            }
        }
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

    /**
     * 导出报价表信息
     * @param pricedSheetCodeCriteria
     * https://pricedproduct-internalapi-external.dsloco.com/api/sheets/exportFile
     * @return
     */
    @Override
    public FileStream exportFile(PricedSheetCodeCriteria pricedSheetCodeCriteria) {
        R<FileStream> exportFile = htpPricedSheetFeignService.exportFile(pricedSheetCodeCriteria);
        return exportFile.getData();
    }

    /**
     * 使用file文件导入产品报价表信息
     * https://pricedproduct-internalapi-external.dsloco.com/api/sheets/importFile
     * @param sheetCode
     * @param file
     * @return
     */
    @Override
    public void importFile(String sheetCode, MultipartFile file) {
        R<ResponseVO> responseVOR = htpPricedSheetFeignService.importFile(sheetCode, file);
        ResponseVO.resultAssert(responseVOR, "使用file文件导入产品报价表信息");
    }

    /**
     * 修改一个计价产品信息的报价表对应的等级和生效时间段
     * https://pricedproduct-internalapi-external.dsloco.com/api/products/{productCode}/{sheetCode}/Grade
     * @param productSheetGradeDTO
     */
    @Override
    public void grade(ProductSheetGradeDTO productSheetGradeDTO) {
        ChangeSheetGradeCommand changeSheetGradeCommand = BeanMapperUtil.map(productSheetGradeDTO, ChangeSheetGradeCommand.class);
        R<ResponseVO> responseVOR = htpPricedProductFeignService.grade(changeSheetGradeCommand);
        ResponseVO.resultAssert(responseVOR, "修改一个计价产品信息的报价表对应的等级和生效时间段");
    }

    /**
     * 根据报价表编号获取产品报价表信息
     * https://pricedproduct-internalapi-external.dsloco.com/api/sheets/{sheetCode}
     * @param sheetCode
     * @return
     */
    @Override
    public PricedSheetExcelInfoVO excelInfo(String sheetCode) {
        R<PricedSheet> info = htpPricedSheetFeignService.info(sheetCode);
        PricedSheet data = info.getData();
        PricedSheetExcelInfoVO vo = new PricedSheetExcelInfoVO().setSheetCode(sheetCode);
        if (data == null) {
            return vo;
        }
        List<PricedZoneVO> zones = BeanMapperUtil.mapList(data.getZones(), PricedZoneVO.class);
        vo.setZones(zones);

        List<PricedChargeVO> charges = data.getCharges().stream().map(charge -> {
            PricedChargeVO chargeVO = new PricedChargeVO();
            chargeVO.setZone(charge.getZone());
            chargeVO.setMinWeight(charge.getMinWeight());
            chargeVO.setMaxWeight(charge.getMaxWeight());
            ChargeFormula formula = charge.getFormula();
            BeanUtils.copyProperties(formula, chargeVO);
            return chargeVO;
        }).collect(Collectors.toList());
        vo.setCharges(charges);

        List<PricedSurchargeVO> surcharges = data.getSurcharges().stream().map(surcharge -> {
            PricedSurchargeVO surchargeVO = new PricedSurchargeVO();
            BeanUtils.copyProperties(surcharge, surchargeVO);

            PackageLimit packageLimit = surcharge.getPackageLimit();
            BeanUtils.copyProperties(packageLimit, surchargeVO);

            Packing minPackingLimit = packageLimit.getMinPackingLimit();
            surchargeVO.setMinPackingLimitStr(minPackingLimit.getLength() + "*" + minPackingLimit.getWidth() + "*" + minPackingLimit.getHeight());
            Packing packingLimit = packageLimit.getPackingLimit();
            surchargeVO.setPackingLimitStr(packingLimit.getLength() + "*" + packingLimit.getWidth() + "*" + packingLimit.getHeight());

            ChargeFormula formula = surcharge.getFormula();
            BeanUtils.copyProperties(formula, surchargeVO);

            return surchargeVO;
        }).collect(Collectors.toList());
        vo.setSurcharges(surcharges);

        List<PricedAddressFilterVO> addressFilter = BeanMapperUtil.mapList(data.getAddressFilter(), PricedAddressFilterVO.class);
        vo.setAddressFilter(addressFilter);

        return vo;
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

            String packingLimit = item.getPackingLimitStr();
            if (StringUtils.isNotEmpty(packingLimit)) {
                String[] split = packingLimit.split("\\*");
                AssertUtil.isTrue(split.length == 3, "包裹总尺寸填写不符合规则(L*W*H)");
                Packing packing = new Packing().setLength(new BigDecimal(split[0])).setWidth(new BigDecimal(split[1])).setHeight(new BigDecimal(split[2])).setLengthUnit("CM");
                packageLimit.setPackingLimit(packing);
            }

            packageLimit.setVolumeLong(item.getVolumeLong());
            packageLimit.setVolume(item.getVolume());
            packageLimit.setPerimeter(item.getPerimeter());
            vw.setPackageLimit(packageLimit);

            return vw;
        }).collect(Collectors.toList());

        PackageLimitVO limitVo = pricedSheetDTO.getLimit();
        PackageLimit limit = BeanMapperUtil.map(limitVo, PackageLimit.class);

        String minPackingLimit = limitVo.getMinPackingLimitStr();
        if (StringUtils.isNotEmpty(minPackingLimit)) {
            String[] split = minPackingLimit.split("\\*");
            AssertUtil.isTrue(split.length == 3, "最小尺寸填写不符合规则(L*W*H)");
            Packing minPacking = new Packing().setLength(new BigDecimal(split[0])).setWidth(new BigDecimal(split[1])).setHeight(new BigDecimal(split[2])).setLengthUnit("CM");
            limit.setMinPackingLimit(minPacking);
        }

        String packingLimit = limitVo.getPackingLimitStr();
        if (StringUtils.isNotEmpty(packingLimit)) {
            String[] split2 = packingLimit.split("\\*");
            AssertUtil.isTrue(split2.length == 3, "最大尺寸填写不符合规则(L*W*H)");
            Packing packing = new Packing().setLength(new BigDecimal(split2[0])).setWidth(new BigDecimal(split2[1])).setHeight(new BigDecimal(split2[2])).setLengthUnit("CM");
            limit.setPackingLimit(packing);
        }

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
