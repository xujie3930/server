package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.*;
import com.szmsd.bas.vo.BaseProductVO;
import com.szmsd.common.core.domain.R;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author l
 * @since 2021-03-04
 */
public interface IBaseProductService extends IService<BaseProduct> {

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    BaseProduct selectBaseProductById(String id);

    /**
     * 查询模块列表
     *
     * @param baseProduct 模块
     * @return 模块集合
     */
    List<BaseProduct> selectBaseProductList(BaseProduct baseProduct);

    /**
     * 查询模块列表
     *
     * @param queryDto
     * @return
     */
    List<BaseProduct> selectBaseProductPage(BaseProductQueryDto queryDto);

    /**
     * 通过code查询
     *
     * @param code
     * @return
     */
    List<BaseProductVO> selectBaseProductByCode(String code,String sellerCode,String category);

    List<BaseProductMeasureDto> batchSKU(List<String> codes);

    /**
     * 导入sku
     * @param list
     */
    void importBaseProduct(List<BaseProductImportDto> list);

    void measuringProduct(MeasuringProductRequest measuringProductRequest);

    /**
     * 查询sku信息
     *
     * @param baseProduct
     * @return
     */
    List<BaseProduct> listSku(BaseProduct baseProduct);

    /**
     * 查询sku信息
     *
     * @param queryDto
     * @return
     */
    List<BaseProduct> listSkuBySeller(BaseProductQueryDto queryDto);

    /**
     * 获取单条sku
     *
     * @param baseProduct
     * @return
     */
    R<BaseProduct> getSku(BaseProduct baseProduct);

    List<BaseProductExportDto> exportProduceList(BaseProductQueryDto queryDto);

    /**
     * 新增模块
     *
     * @param baseProductDto 模块
     * @return 结果
     */
    int insertBaseProduct(BaseProductDto baseProductDto);

    /**
     * 批量新增模块
     *
     * @param baseProductDtos 模块
     * @return 结果
     */
    List<BaseProduct> BatchInsertBaseProduct(List<BaseProductDto> baseProductDtos);

    /**
     * 修改模块
     *
     * @param baseProductDto 模块
     * @return 结果
     */
    int updateBaseProduct(BaseProductDto baseProductDto) throws IllegalAccessException;

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    Boolean deleteBaseProductByIds(List<Long> ids) throws IllegalAccessException;

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    int deleteBaseProductById(String id);

    /**
     * 查询SKU是否有效
     *
     * @param baseProduct
     * @return
     */
    R<Boolean> checkSkuValidToDelivery(BaseProduct baseProduct);

    /**
     * 根据sku返回产品属性
     *
     * @param conditionQueryDto conditionQueryDto
     * @return String
     */
    List<String> listProductAttribute(BaseProductConditionQueryDto conditionQueryDto);

    /**
     * 根据仓库，SKU查询产品信息
     *
     * @param conditionQueryDto conditionQueryDto
     * @return BaseProduct
     */
    List<BaseProduct> queryProductList(BaseProductConditionQueryDto conditionQueryDto);
}

