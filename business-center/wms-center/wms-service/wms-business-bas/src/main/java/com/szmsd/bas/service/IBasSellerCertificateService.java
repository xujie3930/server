package com.szmsd.bas.service;

import com.szmsd.bas.domain.BasSellerCertificate;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
* <p>
    *  服务类
    * </p>
*
* @author l
* @since 2021-03-10
*/
public interface IBasSellerCertificateService extends IService<BasSellerCertificate> {

        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        BasSellerCertificate selectBasSellerCertificateById(String id);

        /**
        * 查询模块列表
        *
        * @param basSellerCertificate 模块
        * @return 模块集合
        */
        List<BasSellerCertificate> selectBasSellerCertificateList(BasSellerCertificate basSellerCertificate);

        /**
        * 新增模块
        *
        * @param basSellerCertificate 模块
        * @return 结果
        */
        int insertBasSellerCertificate(BasSellerCertificate basSellerCertificate);

        /**
        * 修改模块
        *
        * @param basSellerCertificate 模块
        * @return 结果
        */
        int updateBasSellerCertificate(BasSellerCertificate basSellerCertificate);

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        int deleteBasSellerCertificateByIds(List<String> ids);

        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        int deleteBasSellerCertificateById(String id);

}

