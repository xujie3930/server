package com.szmsd.http.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.http.domain.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HtpConfigMapper extends BaseMapper {

    List<HtpUrl> selectHtpUrl(@Param("groupId") String groupId, @Param("serviceId") String serviceId);

    List<HtpUrlGroup> selectHtpUrlGroup(@Param("groupId") String groupId, @Param("groupName") String groupName);

    List<HtpWarehouse> selectHtpWarehouse(@Param("groupId") String groupId, @Param("warehouseCode") String warehouseCode);

    List<HtpWarehouseGroup> selectHtpWarehouseGroup(@Param("groupId") String groupId, @Param("groupName") String groupName);

    List<HtpWarehouseUrlGroup> selectHtpWarehouseUrlGroup(@Param("warehouseGroupId") String warehouseGroupId, @Param("urlGroupId") String urlGroupId);

    HtpUrlGroup selectDefaultHtpUrlGroup();

    void saveHtpUrlGroup(HtpUrlGroup htpUrlGroup);

    int updateHtpUrlGroup(HtpUrlGroup htpUrlGroup);

    void saveHtpUrl(HtpUrl htpUrl);

    int updateHtpUrl(HtpUrl htpUrl);

    void saveHtpWarehouseGroup(HtpWarehouseGroup htpWarehouseGroup);

    int updateHtpWarehouseGroup(HtpWarehouseGroup htpWarehouseGroup);

    void deleteHtpWarehouseByWarehouseCode(String warehouseCode);

    void saveWarehouse(HtpWarehouse htpWarehouse);

    void deleteHtpWarehouseUrlGroupBywarehouseGroupId(String warehouseGroupId);

    void saveHtpWarehouseUrlGroup(HtpWarehouseUrlGroup htpWarehouseUrlGroup);

    void saveDeployLog(HtpDeployLog htpDeployLog);

    void deleteHtpWarehouse(@Param("groupId") String groupId, @Param("warehouseCode") String warehouseCode);

    HtpDeployLog selectLastDeployLog();

}
