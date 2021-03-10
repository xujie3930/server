package com.szmsd.bas.component;

import com.szmsd.bas.dto.PricedProductsDTO;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.http.api.feign.HtpPricedProductFeignService;
import com.szmsd.http.dto.GetPricedProductsCommand;
import com.szmsd.http.vo.DirectServiceFeeData;
import com.szmsd.http.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * 远程请求
 */
@Component
@Slf4j
public class RemoteRequest {

    @Resource
    private HtpPricedProductFeignService htpPricedProductFeignService;

    /**
     * 创建入库单
     * @param createInboundReceiptDTO
     */
    public List<DirectServiceFeeData> pricedProducts(PricedProductsDTO createInboundReceiptDTO) {
        GetPricedProductsCommand getPricedProductsCommand = new GetPricedProductsCommand();
        R<List<DirectServiceFeeData>> listR = htpPricedProductFeignService.pricedProducts(getPricedProductsCommand);
        return listR.getData();
    }

    public void resultAssert(R<? extends ResponseVO> result, String api) {
        AssertUtil.isTrue(result.getCode() == HttpStatus.SUCCESS, "RemoteRequest[" + api + "失败:" +  result.getMsg() + "]");
        ResponseVO data = result.getData();
        AssertUtil.isTrue(data.getSuccess() != null && data.getSuccess() == true, "RemoteRequest[" + api + "失败:" +  getDefaultStr(data.getMessage()).concat(getDefaultStr(data.getErrors())) + "]");
    }

    public String getDefaultStr(String str) {
        return Optional.ofNullable(str).orElse("");
    }

}
