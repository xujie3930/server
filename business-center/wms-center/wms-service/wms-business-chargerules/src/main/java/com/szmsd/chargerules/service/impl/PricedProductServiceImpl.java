package com.szmsd.chargerules.service.impl;

import com.szmsd.chargerules.dto.CreateProductDTO;
import com.szmsd.chargerules.dto.PricedProductQueryDTO;
import com.szmsd.chargerules.service.IPricedProductService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.http.api.feign.HtpPricedProductFeignService;
import com.szmsd.http.dto.CreatePricedProductCommand;
import com.szmsd.http.dto.GetPricedProductsCommand;
import com.szmsd.http.dto.PricedProductSearchCriteria;
import com.szmsd.http.vo.DirectServiceFeeData;
import com.szmsd.http.vo.KeyValuePair;
import com.szmsd.http.vo.PricedProduct;
import com.szmsd.http.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class PricedProductServiceImpl implements IPricedProductService {

    @Resource
    private HtpPricedProductFeignService htpPricedProductFeignService;

    /**
     * 根据包裹基本信息获取可下单报价产品
     * https://pricedproduct-internalapi-external.dsloco.com/api/products/PricedProducts
     * @param getPricedProductsCommand
     * @return
     */
    @Override
    public List<DirectServiceFeeData> pricedProducts(GetPricedProductsCommand getPricedProductsCommand) {
        R<List<DirectServiceFeeData>> listR = htpPricedProductFeignService.pricedProducts(getPricedProductsCommand);
        return R.getDataAndException(listR);
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
        PageVO<PricedProduct> pageResult = htpPricedProductFeignService.pageResult(pricedProductSearchCriteria);
        return TableDataInfo.convert(pageResult);
    }

    /**
     * 创建报价产品信息
     * https://pricedproduct-internalapi-external.dsloco.com/swagger/index.html/api/products
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
        R<ResponseVO> responseVOR = htpPricedProductFeignService.create(createPricedProductCommand);
        ResponseVO.resultAssert(responseVOR, "创建报价产品信息");
        log.info("创建报价产品信息：操作完成");
    }

}
