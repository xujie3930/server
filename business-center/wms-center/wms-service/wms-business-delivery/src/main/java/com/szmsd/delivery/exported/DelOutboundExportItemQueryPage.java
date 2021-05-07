package com.szmsd.delivery.exported;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.common.core.utils.QueryPage;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.delivery.dto.DelOutboundExportItemListDto;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.imported.CacheContext;
import com.szmsd.delivery.service.IDelOutboundDetailService;
import com.szmsd.delivery.vo.DelOutboundExportItemListVO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zhangyuyuan
 * @date 2021-04-23 15:16
 */
public class DelOutboundExportItemQueryPage implements QueryPage<DelOutboundExportItemListVO> {

    private final DelOutboundListQueryDto delOutboundListQueryDto;
    private final QueryDto queryDto;
    private final CacheContext<String, BaseProduct> productCacheContext;
    private final IDelOutboundDetailService delOutboundDetailService;
    private final BaseProductClientService baseProductClientService;

    public DelOutboundExportItemQueryPage(DelOutboundListQueryDto delOutboundListQueryDto, QueryDto queryDto, IDelOutboundDetailService delOutboundDetailService, BaseProductClientService baseProductClientService) {
        this.delOutboundListQueryDto = delOutboundListQueryDto;
        this.queryDto = queryDto;
        this.baseProductClientService = baseProductClientService;
        this.productCacheContext = new CacheContext.MapCacheContext<>();
        this.delOutboundDetailService = delOutboundDetailService;
    }

    @Override
    public Page<DelOutboundExportItemListVO> getPage() {
        PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize());
        Page<DelOutboundExportItemListDto> exportPage = (Page<DelOutboundExportItemListDto>) this.delOutboundDetailService.exportList(delOutboundListQueryDto);
        Page<DelOutboundExportItemListVO> page = new Page<>();
        page.setTotal(exportPage.getTotal());
        page.setPages(exportPage.getPages());
        page.setPageNum(exportPage.getPageNum());
        if (CollectionUtils.isNotEmpty(exportPage)) {
            Set<String> skus = new HashSet<>();
            for (DelOutboundExportItemListDto dto : exportPage) {
                String sku = dto.getSku();
                if (!this.productCacheContext.containsKey(sku)) {
                    skus.add(sku);
                }
            }
            if (!skus.isEmpty()) {
                BaseProductConditionQueryDto baseProductConditionQueryDto = new BaseProductConditionQueryDto();
                baseProductConditionQueryDto.setSkus(new ArrayList<>(skus));
                List<BaseProduct> productList = this.baseProductClientService.queryProductList(baseProductConditionQueryDto);
                if (CollectionUtils.isNotEmpty(productList)) {
                    for (BaseProduct product : productList) {
                        this.productCacheContext.put(product.getCode(), product);
                    }
                }
            }
            for (DelOutboundExportItemListDto dto : exportPage) {
                DelOutboundExportItemListVO vo = BeanMapperUtil.map(dto, DelOutboundExportItemListVO.class);
                BaseProduct product = this.productCacheContext.get(dto.getSku());
                if (null != product) {
                    vo.setDeclaredNameEn(product.getProductName());
                    vo.setProductAttributeName(product.getProductAttributeName());
                }
                page.add(vo);
            }
        }
        return page;
    }

    @Override
    public void nextPage() {
        // 下一页
        delOutboundListQueryDto.setPageNum(delOutboundListQueryDto.getPageNum() + 1);
    }
}
