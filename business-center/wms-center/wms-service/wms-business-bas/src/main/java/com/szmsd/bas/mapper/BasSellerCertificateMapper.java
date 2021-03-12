package com.szmsd.bas.mapper;

import com.szmsd.bas.domain.BasSellerCertificate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author l
 * @since 2021-03-10
 */
public interface BasSellerCertificateMapper extends BaseMapper<BasSellerCertificate> {
    /**
     * 物理删除
     * @param sellerCode
     * @return
     */
    int delBasSellerCertificateByPhysics(@Param("sellerCode") String sellerCode);
}
