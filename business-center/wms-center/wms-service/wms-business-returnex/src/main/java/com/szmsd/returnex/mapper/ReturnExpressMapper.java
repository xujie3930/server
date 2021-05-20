package com.szmsd.returnex.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.common.datascope.annotation.DataScope;
import com.szmsd.returnex.domain.ReturnExpressDetail;
import com.szmsd.returnex.dto.ReturnExpressListQueryDTO;
import com.szmsd.returnex.vo.ReturnExpressListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName: ReturnExpressMapper
 * @Description: 退货
 * @Author: 11
 * @Date: 2021/3/26 11:54
 */
public interface ReturnExpressMapper extends BaseMapper<ReturnExpressDetail> {

    @DataScope("seller_code")
    List<ReturnExpressListVO> selectPageList(@Param(value = "cm") ReturnExpressListQueryDTO queryDto);
}
