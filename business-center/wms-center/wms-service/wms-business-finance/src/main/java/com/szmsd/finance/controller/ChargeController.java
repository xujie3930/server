package com.szmsd.finance.controller;

import com.szmsd.chargerules.api.feign.ChargeFeignService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

@Api(tags = {"费用"})
@RestController
@RequestMapping("/charge")
public class ChargeController extends BaseController {

    @Resource
    private ChargeFeignService chargeFeignService;

    @Resource
    private DelOutboundFeignService delOutboundFeignService;

    @AutoValue
    @ApiOperation(value = "费用查询")
    @GetMapping("/query/page")
    public TableDataInfo<QueryChargeVO> queryPage(QueryChargeDto queryChargeDto) {
        startPage();
        // 子母单的查询 如果没有传值就只能才自己的
        if (Objects.nonNull(SecurityUtils.getLoginUser())) {
            String cusCode = CollectionUtils.isNotEmpty(SecurityUtils.getLoginUser().getPermissions()) ? SecurityUtils.getLoginUser().getPermissions().get(0) : "";
            if (StringUtils.isEmpty(queryChargeDto.getCustomCode())) {
                queryChargeDto.setCustomCode(cusCode);
            }
        }
        if (queryChargeDto.getQueryType() == 1) {
            R<TableDataInfo<QueryChargeVO>> result = chargeFeignService.selectPage(queryChargeDto);
            if (result.getCode() != 200) {
                log.error("selectPage failed. code: {} msg: {}", result.getCode(), result.getMsg());
                return new TableDataInfo<>();
            }
            return result.getData();
        }
        R<TableDataInfo<QueryChargeVO>> result = delOutboundFeignService.getDelOutboundCharge(queryChargeDto);
        if (result.getCode() != 200) {
            log.error("selectPage failed. code: {} msg: {}", result.getCode(), result.getMsg());
            return new TableDataInfo<>();
        }
        return result.getData();

    }
}
