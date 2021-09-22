package com.szmsd.bas.api.service.impl;

import com.szmsd.bas.api.feign.BaseProductFeignService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.constant.ShipmentType;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.bas.dto.BaseProductDto;
import com.szmsd.bas.dto.BaseProductQueryDto;
import com.szmsd.bas.dto.MeasuringProductRequest;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.web.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class BaseProductClientServiceImpl implements BaseProductClientService {

    @Autowired
    private BaseProductFeignService baseProductFeignService;

    @Override
    public TableDataInfo<BaseProduct> list(BaseProductQueryDto queryDto) {
        TableDataInfo<BaseProduct> r = baseProductFeignService.list(queryDto);
        if(Constants.SUCCESS != r.getCode()){
            // 抛出接口返回的异常信息
            throw new CommonException("" + r.getCode(), r.getMsg());
        }
        return r;
    }

    @Override
    public void add(BaseProductDto baseProductDto) {
        R.getDataAndException(this.baseProductFeignService.add(baseProductDto));
    }

    @Override
    public Boolean checkSkuValidToDelivery(String code) {
        BaseProduct baseProduct = new BaseProduct();
        baseProduct.setCode(code);
        return this.baseProductFeignService.checkSkuValidToDelivery(baseProduct).getData();
    }

    @Override
    public List<BaseProduct> listSku(BaseProduct baseProduct) {
        return R.getDataAndException(this.baseProductFeignService.listSku(baseProduct));
    }
    @Override
    public R measuringProduct(MeasuringProductRequest measuringProductRequest) {
        return this.baseProductFeignService.measuringProduct(measuringProductRequest);
    }

    @Override
    public List<String> listProductAttribute(BaseProductConditionQueryDto conditionQueryDto) {
        return R.getDataAndException(this.baseProductFeignService.listProductAttribute(conditionQueryDto));
    }

    @Override
    public String buildShipmentType(String sellerCode, List<String> skus) {
        // fix:SKU不跟仓库关联，SKU跟卖家编码关联。
        BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
        conditionQueryDto.setSellerCode(sellerCode);
        conditionQueryDto.setSkus(skus);
        List<String> listProductAttribute = this.listProductAttribute(conditionQueryDto);
        return ShipmentType.highest(listProductAttribute);
    }

    @Override
    public List<BaseProduct> queryProductList(BaseProductConditionQueryDto conditionQueryDto) {
        return R.getDataAndException(this.baseProductFeignService.queryProductList(conditionQueryDto));
    }
}
