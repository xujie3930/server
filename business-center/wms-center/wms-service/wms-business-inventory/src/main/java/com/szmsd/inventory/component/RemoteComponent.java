package com.szmsd.inventory.component;

import com.szmsd.bas.api.feign.BaseProductFeignService;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductMeasureDto;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.api.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * 远程接口
 *
 * @author liangchao
 * @date 2020/12/21
 */
@Component
@Slf4j
public class RemoteComponent {

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private BaseProductFeignService baseProductFeignService;

    /**
     * 获取登录人信息
     *
     * @return
     */
    public SysUser getLoginUserInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return new SysUser();
        }
        SysUser sysUser = remoteUserService.queryGetInfoByUserId(loginUser.getUserId()).getData();
        return Optional.ofNullable(sysUser).orElseGet(SysUser::new);
    }

    /**
     * 获取SKU信息
     * @param code
     * @return
     */
    public BaseProduct getSku(String code) {
        R<BaseProduct> result = baseProductFeignService.getSku(new BaseProduct().setCode(code));
        BaseProduct sku = Optional.ofNullable(result.getData()).orElseGet(BaseProduct::new);
        log.info("远程接口：查询SKU, code={}, {}", code, sku);
        return sku;
    }

    /**
     * 批量获取sku信息
     * @param codes
     * @return
     */
    public List<BaseProductMeasureDto> listSku(List<String> codes) {
        R<List<BaseProductMeasureDto>> result = baseProductFeignService.batchSKU(codes);
        List<BaseProductMeasureDto> skuList = ListUtils.emptyIfNull(result.getData());
        log.info("远程接口：查询SKU, code={}, {}", codes, skuList);
        return skuList;
    }

}
