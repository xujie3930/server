package com.szmsd.http.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

public interface HtpConfigMapper extends BaseMapper {

    List<Map<String, Object>> selectHtpUrl();

    List<Map<String, String>> selectHtpUrlGroup();

    List<Map<String, String>> selectHtpWarehouse();

    List<Map<String, String>> selectHtpWarehouseGroup();

    List<Map<String, String>> selectHtpWarehouseUrlGroup();

    Map<String, String> selectDefaultHtpUrlGroup();
}
