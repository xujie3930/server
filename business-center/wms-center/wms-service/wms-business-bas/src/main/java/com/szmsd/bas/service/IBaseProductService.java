package com.szmsd.bas.service;

import com.szmsd.bas.domain.BaseProduct;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.dto.BaseProductDto;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
* <p>
    *  服务类
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
        * 新增模块
        *
        * @param baseProductDto 模块
        * @return 结果
        */
        int insertBaseProduct(BaseProductDto baseProductDto);

        /**
        * 修改模块
        *
        * @param baseProduct 模块
        * @return 结果
        */
        int updateBaseProduct(BaseProduct baseProduct);

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        int deleteBaseProductByIds(List<String> ids);

        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        int deleteBaseProductById(String id);

        /**
         * 查询SKU是否有效
         * @param baseProduct
         * @return
         */
        R<Boolean> checkSkuValidToDelivery(BaseProduct baseProduct);

}

