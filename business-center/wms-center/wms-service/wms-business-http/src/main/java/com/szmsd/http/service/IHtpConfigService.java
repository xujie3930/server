package com.szmsd.http.service;

import com.szmsd.http.domain.*;

import java.util.List;

public interface IHtpConfigService {

    void loadHtpConfig(String remark);

    void saveDeployLog(String content, String remark);

    HtpDeployLog selectLastDeployLog();

    List<HtpUrlGroup> selectHtpUrlGroup();

    HtpUrlGroup selectHtpUrlGroup(String groupId);

    List<HtpUrl> selectHtpUrl(HtpUrl htpUrl);

    List<HtpWarehouseGroup> selectHtpWarehouseGroup();

    List<HtpWarehouse> selectHtpWarehouse(HtpWarehouse htpWarehouse);

    List<HtpWarehouseUrlGroup> selectHtpWarehouseUrlGroup(String warehouseGroupId, String urlGroupId);

    void saveOrUpdateHtpUrlGroup(HtpUrlGroup htpUrlGroup);

    void setHtpUrlGroupDefault(String groupId);

    void saveOrUpdateHtpUrl(HtpUrl htpUrl);

    void saveOrUpdateHtpWarehouseGroup(HtpWarehouseGroup htpWarehouseGroup);

    void saveWarehouse(HtpWarehouse htpWarehouse);

    void saveHtpWarehouseUrlGroup(HtpWarehouseUrlGroup htpWarehouseUrlGroup);

    void deleteHtpWarehouse(HtpWarehouse htpWarehouse);

    void deleteHtpGroup(String groupId);

    void deleteHtpWarehouseGroup(String groupId);
}
