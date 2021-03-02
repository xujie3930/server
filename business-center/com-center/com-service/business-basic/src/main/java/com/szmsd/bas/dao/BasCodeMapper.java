package com.szmsd.bas.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.bas.domain.BasCode;
import com.szmsd.bas.domain.BasCodeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author lufei
 * @version 1.0
 * @Date 2020-06-29 9:48
 * @Description
 */
@Mapper
public interface BasCodeMapper extends BaseMapper<BasCode> {

    /**
     * 查询列表数据
     * @param basCodeDto
     * @return
     */
    List<BasCode> list(BasCodeDto basCodeDto);

    /**
     * 保存数据
     * @param basCode
     * @return
     */
    int save(BasCode basCode);

    /**
     * 修改数据
     * @param basCode
     * @return
     */
    int update(BasCode basCode);

    /**
     * 锁住当前行
     * @param id
     * @return
     */
    BasCode getLock(String id);

}
