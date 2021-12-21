package com.szmsd.http.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.http.domain.CommonRemote;
import com.szmsd.http.dto.CommonScanQueryDTO;
import com.szmsd.http.vo.CommonScanListVO;

import java.util.List;

/**
 * <p>
 * 扫描执行任务 Mapper 接口
 * </p>
 *
 * @author huanggaosheng
 * @since 2021-11-10
 */
public interface CommonScanMapper extends BaseMapper<CommonRemote> {

    List<CommonScanListVO> queryWarnList(CommonScanQueryDTO commonScanQueryDTO);
}
