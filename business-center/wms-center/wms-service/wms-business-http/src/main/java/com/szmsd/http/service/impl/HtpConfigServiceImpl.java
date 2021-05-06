package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.text.UUID;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.config.inner.UrlGroupConfig;
import com.szmsd.http.domain.*;
import com.szmsd.http.mapper.HtpConfigMapper;
import com.szmsd.http.service.IHtpConfigService;
import com.szmsd.system.api.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 外部接口服务配置
 */
@Slf4j
@Service
public class HtpConfigServiceImpl implements IHtpConfigService {

    @Resource
    private HtpConfigMapper htpConfigMapper;

    @Resource
    private HttpConfig httpConfig;

    /**
     * 加载服务配置
     */
    @Override
    public void loadHtpConfig(String remark) {
        // 加载之前
        httpConfig.loadBefore();
        try {
            List<HtpUrl> htpUrls = htpConfigMapper.selectHtpUrl(null, null);
            Map<String, List<HtpUrl>> group = htpUrls.stream().collect(Collectors.groupingBy(HtpUrl::getGroupId));
            Map<String, UrlGroupConfig> collect = group.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> {
                Map<String, Object> service = e.getValue().stream().collect(Collectors.toMap(HtpUrl::getServiceId, v -> {
                    try {
                        Optional.ofNullable(v.getHeaders()).ifPresent(item -> v.setHeaders(JSONObject.parseObject(item.toString())));
                    } catch (Exception ex) {
                        log.error(ex.getMessage());
                    }
                    return v;
                }));
                return JSONObject.parseObject(JSON.toJSONString(service), UrlGroupConfig.class);
            }));
            httpConfig.setUrlGroup(collect);

            List<HtpWarehouse> htpWarehouses = htpConfigMapper.selectHtpWarehouse(null, null);
            Map<String, Set<String>> warehouseGroup = htpWarehouses.stream().collect(Collectors.groupingBy(HtpWarehouse::getGroupId, Collectors.collectingAndThen(Collectors.toSet(), e -> e.stream().map(HtpWarehouse::getWarehouseCode).collect(Collectors.toSet()))));
            httpConfig.setWarehouseGroup(warehouseGroup);

            List<HtpWarehouseUrlGroup> htpWarehouseUrlGroups = htpConfigMapper.selectHtpWarehouseUrlGroup(null, null);
            Map<String, String> mapperGroup = htpWarehouseUrlGroups.stream().collect(Collectors.toMap(HtpWarehouseUrlGroup::getWarehouseGroupId, HtpWarehouseUrlGroup::getUrlGroupId));
            httpConfig.setMapperGroup(mapperGroup);

            HtpUrlGroup htpUrlGroup = htpConfigMapper.selectDefaultHtpUrlGroup();
            if (htpUrlGroup == null) {
                String defaultUrlGroupId = new ArrayList(collect.keySet()).get(0).toString();
                httpConfig.setDefaultUrlGroup(defaultUrlGroupId);
                this.setHtpUrlGroupDefault(defaultUrlGroupId);
                HtpUrlGroup htpUrlGroup1 = this.selectHtpUrlGroup(defaultUrlGroupId);
                remark = remark == null ? "" : remark;
                remark += ", 设置[" + htpUrlGroup1.getGroupName() + "]为默认组";
            } else {
                httpConfig.setDefaultUrlGroup(htpUrlGroup.getGroupId());
            }
            this.saveDeployLog(JSON.toJSONString(httpConfig), remark);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            httpConfig.loadError(e);
            throw e;
        } finally {
            httpConfig.loadAfter();
        }
    }

    /**
     * 保存部署日志
     *
     * @param content
     * @param remark
     */
    @Override
    public void saveDeployLog(String content, String remark) {
        HtpDeployLog htpDeployLog = new HtpDeployLog();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            htpDeployLog.setCreateBy("1");
            htpDeployLog.setCreateByName("admin");
        } else {
            htpDeployLog.setCreateBy(loginUser.getUserId().toString());
            htpDeployLog.setCreateByName(loginUser.getUsername());
        }
        htpDeployLog.setContent(content);
        htpDeployLog.setRemark(remark);
        htpConfigMapper.saveDeployLog(htpDeployLog);
    }

    /**
     * 查询最近一次部署
     *
     * @return
     */
    @Override
    public HtpDeployLog selectLastDeployLog() {
        return htpConfigMapper.selectLastDeployLog();
    }

    /**
     * 地址组集合
     *
     * @return
     */
    @Override
    public List<HtpUrlGroup> selectHtpUrlGroup() {
        return htpConfigMapper.selectHtpUrlGroup(null, null);
    }

    /**
     * 地址组
     *
     * @return
     */
    @Override
    public HtpUrlGroup selectHtpUrlGroup(String groupId) {
        HtpUrlGroup htpUrlGroup = null;
        List<HtpUrlGroup> htpUrlGroups = htpConfigMapper.selectHtpUrlGroup(groupId, null);
        if (CollectionUtils.isNotEmpty(htpUrlGroups)) {
            htpUrlGroup = htpUrlGroups.get(0);
        }
        return htpUrlGroup;
    }

    /**
     * 地址集合
     *
     * @param htpUrl
     * @return
     */
    @Override
    public List<HtpUrl> selectHtpUrl(HtpUrl htpUrl) {
        return htpConfigMapper.selectHtpUrl(htpUrl.getGroupId(), htpUrl.getServiceId());
    }

    /**
     * 仓库组集合
     *
     * @return
     */
    @Override
    public List<HtpWarehouseGroup> selectHtpWarehouseGroup() {
        return htpConfigMapper.selectHtpWarehouseGroup(null, null);
    }

    /**
     * 仓库分组集合
     *
     * @param htpWarehouse
     * @return
     */
    @Override
    public List<HtpWarehouse> selectHtpWarehouse(HtpWarehouse htpWarehouse) {
        return htpConfigMapper.selectHtpWarehouse(htpWarehouse.getGroupId(), htpWarehouse.getWarehouseCode());
    }

    /**
     * 查询仓库组关联地址组
     *
     * @param warehouseGroupId
     * @param urlGroupId
     * @return
     */
    @Override
    public List<HtpWarehouseUrlGroup> selectHtpWarehouseUrlGroup(String warehouseGroupId, String urlGroupId) {
        return htpConfigMapper.selectHtpWarehouseUrlGroup(warehouseGroupId, urlGroupId);
    }

    /**
     * 地址分组表【保存/修改】
     *
     * @param htpUrlGroup
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveOrUpdateHtpUrlGroup(HtpUrlGroup htpUrlGroup) {
        AssertUtil.isTrue(StringUtils.isNotEmpty(htpUrlGroup.getGroupName()), "请输入地址组名称");
        htpUrlGroup.setGroupName(htpUrlGroup.getGroupName().trim());
        List<HtpUrlGroup> htpUrlGroups = htpConfigMapper.selectHtpUrlGroup(null, htpUrlGroup.getGroupName());
        if (CollectionUtils.isNotEmpty(htpUrlGroups)) {
            AssertUtil.isTrue(htpUrlGroups.get(0).getGroupId().equals(htpUrlGroup.getGroupId()), htpUrlGroup.getGroupName() + "已存在");
        }
        if (StringUtils.isEmpty(htpUrlGroup.getDefaultFlag())) {
            htpUrlGroup.setDefaultFlag("0");
        }
        SysUser loginUser = getLoginUser();
        if ("1".equals(htpUrlGroup.getDefaultFlag())) {
            htpConfigMapper.updateHtpUrlGroup(new HtpUrlGroup().setDefaultFlag("0"));
        }
        if (StringUtils.isEmpty(htpUrlGroup.getGroupId())) {
            htpUrlGroup.setGroupId(UUID.fastUUID().toString().replaceAll("-", ""));
            htpUrlGroup.setCreateBy(loginUser.getUserId().toString());
            htpUrlGroup.setCreateByName(loginUser.getUserName());
            htpConfigMapper.saveHtpUrlGroup(htpUrlGroup);
        } else {
            htpUrlGroup.setUpdateBy(loginUser.getUserId().toString());
            htpUrlGroup.setUpdateByName(loginUser.getUserName());
            int i = htpConfigMapper.updateHtpUrlGroup(htpUrlGroup);
            AssertUtil.isTrue(i < 2, "分组修改异常");
            AssertUtil.isTrue(i > 0, "分组不存在请刷新重试");
        }
        if (!"1".equals(htpUrlGroup.getDefaultFlag()) && htpConfigMapper.selectDefaultHtpUrlGroup() == null) {
            htpConfigMapper.updateHtpUrlGroup(htpUrlGroup.setDefaultFlag("1"));
        }
    }

    /**
     * 设置默认
     *
     * @param groupId
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void setHtpUrlGroupDefault(String groupId) {

        HtpUrlGroup htpUrlGroup = new HtpUrlGroup().setDefaultFlag("0");
        htpConfigMapper.updateHtpUrlGroup(htpUrlGroup);

        SysUser loginUser = getLoginUser();
        htpUrlGroup.setGroupId(groupId);
        htpUrlGroup.setDefaultFlag("1");
        htpUrlGroup.setUpdateBy(loginUser.getUserId().toString());
        htpUrlGroup.setUpdateByName(loginUser.getUserName());
        int i = htpConfigMapper.updateHtpUrlGroup(htpUrlGroup);

        AssertUtil.isTrue(i == 1, "分组不存在请刷新重试");
    }

    /**
     * 地址表
     *
     * @param htpUrl
     */
    @Override
    public void saveOrUpdateHtpUrl(HtpUrl htpUrl) {
        if (htpUrl.getHeaders() == null) {
            htpUrl.setHeaders("{}");
        }
        String groupId = htpUrl.getGroupId();
        AssertUtil.isTrue(StringUtils.isNotEmpty(groupId), "请设置分组");
        List<HtpUrlGroup> htpUrlGroups = htpConfigMapper.selectHtpUrlGroup(groupId, null);
        AssertUtil.isTrue(CollectionUtils.isNotEmpty(htpUrlGroups), "分组不存在，请刷新重试");

        String serviceId = htpUrl.getServiceId();
        AssertUtil.notNull(serviceId, "请设置服务");

        String url = htpUrl.getUrl() == null ? null : htpUrl.getUrl().trim();
        AssertUtil.isTrue(StringUtils.isNotEmpty(url), "请填写请求地址");

        SysUser loginUser = getLoginUser();
        List<HtpUrl> htpUrls = htpConfigMapper.selectHtpUrl(groupId, serviceId);
        if (CollectionUtils.isEmpty(htpUrls)) {
            htpUrl.setCreateBy(loginUser.getUserId().toString());
            htpUrl.setCreateByName(loginUser.getUserName());
            htpConfigMapper.saveHtpUrl(htpUrl);
        } else {
            htpUrl.setUpdateBy(loginUser.getUserId().toString());
            htpUrl.setUpdateByName(loginUser.getUserName());
            htpConfigMapper.updateHtpUrl(htpUrl);
        }
    }

    /**
     * 仓库组表【保存/修改】
     *
     * @param htpWarehouseGroup
     */
    @Override
    public void saveOrUpdateHtpWarehouseGroup(HtpWarehouseGroup htpWarehouseGroup) {
        String groupName = htpWarehouseGroup.getGroupName();
        AssertUtil.isTrue(StringUtils.isNotEmpty(groupName), "请输入地址组名称");
        htpWarehouseGroup.setGroupName(htpWarehouseGroup.getGroupName().trim());
        List<HtpWarehouseGroup> htpWarehouseGroups = htpConfigMapper.selectHtpWarehouseGroup(null, groupName);
        if (CollectionUtils.isNotEmpty(htpWarehouseGroups)) {
            AssertUtil.isTrue(htpWarehouseGroups.get(0).getGroupId().equals(htpWarehouseGroup.getGroupId()), htpWarehouseGroup.getGroupName() + "已存在");
        }
        SysUser loginUser = getLoginUser();
        if (StringUtils.isEmpty(htpWarehouseGroup.getGroupId())) {
            htpWarehouseGroup.setGroupId(UUID.fastUUID().toString().replaceAll("-", ""));
            htpWarehouseGroup.setCreateBy(loginUser.getUserId().toString());
            htpWarehouseGroup.setCreateByName(loginUser.getUserName());
            htpConfigMapper.saveHtpWarehouseGroup(htpWarehouseGroup);
        } else {
            htpWarehouseGroup.setUpdateBy(loginUser.getUserId().toString());
            htpWarehouseGroup.setUpdateByName(loginUser.getUserName());
            int i = htpConfigMapper.updateHtpWarehouseGroup(htpWarehouseGroup);
            AssertUtil.isTrue(i < 2, "分组修改异常");
            AssertUtil.isTrue(i > 0, "分组不存在请刷新重试");
        }
    }

    /**
     * 仓库组仓库关联表【保存】
     *
     * @param htpWarehouse
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveWarehouse(HtpWarehouse htpWarehouse) {
        String groupId = htpWarehouse.getGroupId();
        AssertUtil.isTrue(StringUtils.isNotEmpty(groupId), "请设置分组");
        String warehouseCode = htpWarehouse.getWarehouseCode();
        AssertUtil.isTrue(StringUtils.isNotEmpty(warehouseCode), "请设置仓库");
        List<HtpWarehouseGroup> htpWarehouseGroups = htpConfigMapper.selectHtpWarehouseGroup(groupId, null);
        AssertUtil.isTrue(CollectionUtils.isNotEmpty(htpWarehouseGroups), "分组不存在，请刷新重试");
        htpConfigMapper.deleteHtpWarehouseByWarehouseCode(warehouseCode);
        htpConfigMapper.saveWarehouse(htpWarehouse);
    }

    /**
     * 仓库组关联地址组【保存】
     *
     * @param htpWarehouseUrlGroup
     */
    @Override
    public void saveHtpWarehouseUrlGroup(HtpWarehouseUrlGroup htpWarehouseUrlGroup) {

        String warehouseGroupId = htpWarehouseUrlGroup.getWarehouseGroupId();
        AssertUtil.isTrue(StringUtils.isNotEmpty(warehouseGroupId), "请设置仓库组");

        String urlGroupId = htpWarehouseUrlGroup.getUrlGroupId();
        AssertUtil.isTrue(StringUtils.isNotEmpty(urlGroupId), "请设置地址组");

        List<HtpWarehouseGroup> htpWarehouseGroups = htpConfigMapper.selectHtpWarehouseGroup(warehouseGroupId, null);
        AssertUtil.isTrue(CollectionUtils.isNotEmpty(htpWarehouseGroups), "仓库组不存在，请刷新重试");

        List<HtpUrlGroup> htpUrlGroups = htpConfigMapper.selectHtpUrlGroup(urlGroupId, null);
        AssertUtil.isTrue(CollectionUtils.isNotEmpty(htpUrlGroups), "地址组不存在，请刷新重试");

        htpConfigMapper.deleteHtpWarehouseUrlGroupByWarehouseGroupId(warehouseGroupId);
        htpConfigMapper.saveHtpWarehouseUrlGroup(htpWarehouseUrlGroup);

    }

    /**
     * 仓库组仓库关联【移除仓库】 把仓库从改组移除
     *
     * @param htpWarehouse
     */
    @Override
    public void deleteHtpWarehouse(HtpWarehouse htpWarehouse) {
        htpConfigMapper.deleteHtpWarehouse(htpWarehouse.getGroupId(), htpWarehouse.getWarehouseCode());
    }

    /**
     * 删除地址组
     * 空组可以删除
     * @param groupId
     */
    @Override
    public void deleteHtpGroup(String groupId) {
        List<HtpUrl> htpUrls = this.selectHtpUrl(new HtpUrl().setGroupId(groupId));
        AssertUtil.isTrue(CollectionUtils.isEmpty(htpUrls), "该组绑定了相关地址，不能删除");
        htpConfigMapper.deleteHtpGroup(groupId);
    }

    /**
     * 删除仓库组
     * 空组可以删除
     * @param groupId
     */
    @Override
    public void deleteHtpWarehouseGroup(String groupId) {
        List<HtpWarehouse> htpUrls = this.selectHtpWarehouse(new HtpWarehouse().setGroupId(groupId));
        AssertUtil.isTrue(CollectionUtils.isEmpty(htpUrls), "该组绑定了相关仓库，不能删除");
        htpConfigMapper.deleteHtpWarehouseGroup(groupId);
    }

    private SysUser getLoginUser() {
        SysUser sysUser = new SysUser();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            sysUser.setUserId(1L);
            sysUser.setUserName("admin");
        } else {
            sysUser.setUserId(loginUser.getUserId());
            sysUser.setUserName(loginUser.getUsername());
        }
        return sysUser;
    }

}
