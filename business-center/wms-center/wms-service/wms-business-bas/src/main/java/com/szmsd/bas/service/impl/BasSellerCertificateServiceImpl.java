package com.szmsd.bas.service.impl;

import com.szmsd.bas.domain.BasSellerCertificate;
import com.szmsd.bas.mapper.BasSellerCertificateMapper;
import com.szmsd.bas.service.IBasSellerCertificateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;

import javax.annotation.Resource;
import java.util.List;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author l
* @since 2021-03-10
*/
@Service
public class BasSellerCertificateServiceImpl extends ServiceImpl<BasSellerCertificateMapper, BasSellerCertificate> implements IBasSellerCertificateService {

    @Resource
    private BasSellerCertificateMapper basSellerCertificateMapper;
        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        @Override
        public BasSellerCertificate selectBasSellerCertificateById(String id)
        {
        return baseMapper.selectById(id);
        }

        @Override
        public int delBasSellerCertificateByPhysics(String sellerCode)
        {
            return basSellerCertificateMapper.delBasSellerCertificateByPhysics(sellerCode);
        }
        /**
        * 查询模块列表
        *
        * @param basSellerCertificate 模块
        * @return 模块
        */
        @Override
        public List<BasSellerCertificate> selectBasSellerCertificateList(BasSellerCertificate basSellerCertificate)
        {
        QueryWrapper<BasSellerCertificate> where = new QueryWrapper<BasSellerCertificate>();
        return baseMapper.selectList(where);
        }

        /**
        * 新增模块
        *
        * @param basSellerCertificate 模块
        * @return 结果
        */
        @Override
        public int insertBasSellerCertificate(BasSellerCertificate basSellerCertificate)
        {
        return baseMapper.insert(basSellerCertificate);
        }

    /**
     * 批量新增
     * @param basSellerCertificateList
     * @return
     */
    @Override
        public Boolean insertBasSellerCertificateList(List<BasSellerCertificate> basSellerCertificateList){
            return super.saveBatch(basSellerCertificateList);
        }

        /**
        * 修改模块
        *
        * @param basSellerCertificate 模块
        * @return 结果
        */
        @Override
        public int updateBasSellerCertificate(BasSellerCertificate basSellerCertificate)
        {
        return baseMapper.updateById(basSellerCertificate);
        }

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        @Override
        public int deleteBasSellerCertificateByIds(List<String>  ids)
       {
            return baseMapper.deleteBatchIds(ids);
       }

        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        @Override
        public int deleteBasSellerCertificateById(String id)
        {
        return baseMapper.deleteById(id);
        }



    }

