package com.szmsd.chargerules.service.impl;

import cn.hutool.core.date.DateUtil;
import com.szmsd.chargerules.domain.ChaLevelMaintenanceDtoQuery;
import com.szmsd.chargerules.service.IChaLevelMaintenanceService;
import com.szmsd.chargerules.service.ICustomPricesService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.http.api.feign.HtpChaLevelMaintenanceFeignService;
import com.szmsd.http.api.feign.HtpCustomPricesFeignService;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenanceDto;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenancePageRequest;
import com.szmsd.http.dto.custom.*;
import com.szmsd.http.vo.Operation;
import com.szmsd.http.vo.Operator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author admin
* @since 2022-06-22
*/
@Service
public class CustomPricesServiceImpl implements ICustomPricesService {

    private HtpCustomPricesFeignService htpCustomPricesFeignService;

    @Override
    public R updateDiscountDetail(CustomDiscountMainDto dto) {
        return htpCustomPricesFeignService.updateDiscountDetail(dto);
    }

    @Override
    public R updateGradeDetail(CustomGradeMainDto dto) {
        return htpCustomPricesFeignService.updateGradeDetail(dto);
    }

    @Override
    public R<CustomPricesMainDto> result(String clientCode) {
        return htpCustomPricesFeignService.result(clientCode);
    }

    @Override
    public R updateDiscount(UpdateCustomDiscountMainDto dto) {
        return htpCustomPricesFeignService.updateDiscount(dto);
    }

    @Override
    public R updateGrade(UpdateCustomGradeMainDto dto) {
        return htpCustomPricesFeignService.updateGrade(dto);
    }

    @Override
    public R discountDetailResult(String id) {
        return htpCustomPricesFeignService.discountDetailResult(id);
    }

    @Override
    public R gradeDetailResult(String id) {
        return htpCustomPricesFeignService.gradeDetailResult(id);
    }
}

