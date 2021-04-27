package com.szmsd.chargerules.service.impl;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.bas.api.feign.BasWarehouseFeignService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.chargerules.dto.CreateProductDTO;
import com.szmsd.chargerules.dto.FreightCalculationDTO;
import com.szmsd.chargerules.dto.PricedProductQueryDTO;
import com.szmsd.chargerules.dto.UpdateProductDTO;
import com.szmsd.chargerules.service.IPricedProductService;
import com.szmsd.chargerules.vo.FreightCalculationVO;
import com.szmsd.chargerules.vo.PricedProductInfoVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.http.api.feign.HtpPricedProductFeignService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.PricedProduct;
import com.szmsd.http.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PricedProductServiceImpl implements IPricedProductService {

    @Resource
    private HtpPricedProductFeignService htpPricedProductFeignService;

    @Resource
    private BasRegionFeignService basCountryFeignService;

    @Resource
    private BasWarehouseFeignService basWarehouseFeignService;

    /**
     * 根据包裹基本信息获取可下单报价产品
     * https://pricedproduct-internalapi-external.dsloco.com/api/products/PricedProducts
     * @param freightCalculationDTO
     * @return
     */
    @Override
    public List<FreightCalculationVO> pricedProducts(FreightCalculationDTO freightCalculationDTO) {
        GetPricedProductsCommand getPricedProductsCommand = new GetPricedProductsCommand();
        getPricedProductsCommand.setClientCode(freightCalculationDTO.getClientCode());
        getPricedProductsCommand.setShipmentType(freightCalculationDTO.getShipmentType());

        PackageInfo packageInfo = new PackageInfo();
        packageInfo.setWeight(new Weight().setValue(freightCalculationDTO.getWeight()).setUnit("KG"));
        packageInfo.setPacking(new Packing().setLength(freightCalculationDTO.getExtent()).setWidth(freightCalculationDTO.getWidth()).setHeight(freightCalculationDTO.getHeight()).setLengthUnit("CM"));
        packageInfo.setQuantity(freightCalculationDTO.getQuantity());
        getPricedProductsCommand.setPackageInfos(Arrays.asList(packageInfo));

        Address fromAddress = new Address();
        String dealPoint = freightCalculationDTO.getDealPoint();
        R<BasWarehouse> warehouseR = basWarehouseFeignService.queryByWarehouseCode(dealPoint);
        if (warehouseR != null && null != warehouseR.getData()) {
            BasWarehouse warehouse = warehouseR.getData();
            fromAddress.setStreet1(warehouse.getStreet1()).setStreet2(warehouse.getStreet2());
            fromAddress.setProvince(warehouse.getProvince()).setCity(warehouse.getCity());
            fromAddress.setPostCode(warehouse.getPostcode());
            fromAddress.setCountry(new CountryInfo(warehouse.getCountryCode(), null, warehouse.getCountryName(), warehouse.getCountryChineseName()));
        }
        getPricedProductsCommand.setFromAddress(fromAddress);

        Address toAddress = new Address();
        R<BasRegionSelectListVO> countryR = basCountryFeignService.queryByCountryCode(freightCalculationDTO.getCountry());
        if (countryR != null && null != countryR.getData()) {
            BasRegionSelectListVO country = countryR.getData();
            toAddress.setCountry(new CountryInfo(country.getAddressCode(), null, country.getEnName(), country.getName()));
        }
        toAddress.setPostCode(freightCalculationDTO.getPostCode());
        getPricedProductsCommand.setToAddress(toAddress);

        getPricedProductsCommand.setToContactInfo(new ContactInfo());

        R<List<DirectServiceFeeData>> listR = htpPricedProductFeignService.pricedProducts(getPricedProductsCommand);
        if (listR == null || CollectionUtils.isEmpty(listR.getData())) {
            return new ArrayList<>();
        }
        List<FreightCalculationVO> result = listR.getData().stream().map(directServiceFeeData -> {
            FreightCalculationVO freightCalculationVO = new FreightCalculationVO();
            freightCalculationVO.setProductCode(directServiceFeeData.getProductCode());
            freightCalculationVO.setCalcWeight(directServiceFeeData.getCalcWeight().getValue());
            freightCalculationVO.setCalcType(directServiceFeeData.getCalcType());
            freightCalculationVO.setTotalAmount(directServiceFeeData.getChargeItems().getTotalAmount());

            String amountDetail = directServiceFeeData.getChargeItems().getDetails().stream().map(chargeItem -> MessageFormat.format("【{0}】：{1} - {2}", chargeItem.getDescription(), chargeItem.getCurrency(), chargeItem.getPrice())).collect(Collectors.joining(";"));
            freightCalculationVO.setAmountDetail(amountDetail);

            String timeliness = MessageFormat.format("{0} - {1} days", directServiceFeeData.getTimelinessMin(), directServiceFeeData.getTimelinessMax());
            freightCalculationVO.setTimeliness(timeliness);

            return freightCalculationVO;
        }).collect(Collectors.toList());
        return result;
    }

    /**
     * 查询产品下拉列表，返回list数据
     * https://pricedproduct-internalapi-external.dsloco.com/api/products/http/keyValuePairs
     * @return
     */
    @Override
    public List<KeyValuePair> keyValuePairs() {
        R<List<KeyValuePair>> listR = htpPricedProductFeignService.keyValuePairs();
        return R.getDataAndException(listR);
    }

    /**
     * 分页查询产品列表，返回指定页面的数据，以及统计总记录数
     * https://pricedproduct-internalapi-external.dsloco.com/api/products/pageResult
     * @param pricedProductQueryDTO
     * @return
     */
    @Override
    public TableDataInfo<PricedProduct> selectPage(PricedProductQueryDTO pricedProductQueryDTO) {
        PricedProductSearchCriteria pricedProductSearchCriteria = new PricedProductSearchCriteria();
        pricedProductSearchCriteria.setPageNumber(pricedProductQueryDTO.getPageNum());
        pricedProductSearchCriteria.setPageSize(pricedProductQueryDTO.getPageSize());
        pricedProductSearchCriteria.setCode(pricedProductQueryDTO.getCode());
        log.info("分页查询产品列表：{}", pricedProductSearchCriteria);
        R<PageVO<PricedProduct>> pageResult = htpPricedProductFeignService.pageResult(pricedProductSearchCriteria);
        AssertUtil.notNull(pageResult.getData(), pageResult.getMsg());
        return TableDataInfo.convert(pageResult.getData());
    }

    /**
     * 创建报价产品信息
     * https://pricedproduct-internalapi-external.dsloco.com/api/products
     * @param createProductDTO
     */
    @Override 
    public void create(CreateProductDTO createProductDTO) {
        log.info("创建报价产品信息：{}", createProductDTO);
        CreatePricedProductCommand createPricedProductCommand = new CreatePricedProductCommand();
        createPricedProductCommand.setCode(createProductDTO.getCode());
        createPricedProductCommand.setName(createProductDTO.getName());
        createPricedProductCommand.setType(createProductDTO.getType());
        createPricedProductCommand.setCategory(createProductDTO.getCategory());
        createPricedProductCommand.setOverdueDay(createProductDTO.getOverdueDay());
        createPricedProductCommand.setShipmentTypeSupported(createProductDTO.getShipmentTypeSupported());
        createPricedProductCommand.setLogisticsRouteId(createProductDTO.getLogisticsRouteId());
        createPricedProductCommand.setTerminalCarrier(createProductDTO.getTerminalCarrier());
        R<ResponseVO> responseVOR = htpPricedProductFeignService.create(createPricedProductCommand);
        ResponseVO.resultAssert(responseVOR, "创建报价产品信息");
        log.info("创建报价产品信息：操作完成");
    }

    /**
     * 根据产品代码获取计价产品信息
     * https://pricedproduct-internalapi-external.dsloco.com/api/products/{productCode}
     * @param productCode
     * @return
     */
    @Override
    public PricedProductInfoVO getInfo(String productCode) {
        log.info("根据产品代码获取计价产品信息：productCode={}", productCode);
        R<PricedProductInfo> info = htpPricedProductFeignService.info(productCode);
        log.info("根据产品代码获取计价产品信息：{}", info);
        PricedProductInfo data = info.getData();
        PricedProductInfoVO pricedProductInfoVO = data == null ? null : BeanMapperUtil.map(data, PricedProductInfoVO.class);
        return pricedProductInfoVO;
    }

    /**
     * 修改报价产品信息
     * https://pricedproduct-internalapi-external.dsloco.com/api/products
     * @param updateProductDTO
     */
    @Override
    public void update(UpdateProductDTO updateProductDTO) {
        log.info("修改报价产品信息：{}", updateProductDTO);
        UpdatePricedProductCommand updatePricedProductCommand = BeanMapperUtil.map(updateProductDTO, UpdatePricedProductCommand.class);
        R<ResponseVO> responseVOR = htpPricedProductFeignService.update(updatePricedProductCommand);
        ResponseVO.resultAssert(responseVOR, "修改报价产品信息");
        log.info("创建报价产品信息：操作完成");
    }

    /**
     * 导出产品信息列表
     * https://pricedproduct-internalapi-external.dsloco.com/api/products/exportFile
     * @param codes
     * @return
     */
    @Override
    public FileStream exportFile(List<String> codes) {
        R<FileStream> exportFile = htpPricedProductFeignService.exportFile(new PricedProductCodesCriteria().setCodes(codes));
        return exportFile.getData();
    }

}
