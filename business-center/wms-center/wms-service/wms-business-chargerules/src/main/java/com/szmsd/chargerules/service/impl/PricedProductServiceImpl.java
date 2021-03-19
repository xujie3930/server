package com.szmsd.chargerules.service.impl;

import com.szmsd.chargerules.dto.PricedProductQueryDTO;
import com.szmsd.chargerules.service.IPricedProductService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.http.api.feign.HtpPricedProductFeignService;
import com.szmsd.http.dto.GetPricedProductsCommand;
import com.szmsd.http.dto.PricedProductSearchCriteria;
import com.szmsd.http.vo.DirectServiceFeeData;
import com.szmsd.http.vo.KeyValuePair;
import com.szmsd.http.vo.PricedProduct;
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
    public TableDataInfo<PricedProduct> selectList(PricedProductQueryDTO pricedProductQueryDTO) {
        PricedProductSearchCriteria pricedProductSearchCriteria = new PricedProductSearchCriteria();
        pricedProductSearchCriteria.setCode(pricedProductQueryDTO.getCode());
        log.info("分页查询产品列表：{}", pricedProductSearchCriteria);
        PageVO<PricedProduct> pageResult = htpPricedProductFeignService.pageResult(pricedProductSearchCriteria);
        return TableDataInfo.convert(pageResult);
    }

}
