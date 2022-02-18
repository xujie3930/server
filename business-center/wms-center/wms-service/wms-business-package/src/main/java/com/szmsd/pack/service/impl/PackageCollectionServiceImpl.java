package com.szmsd.pack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.pack.domain.PackageCollection;
import com.szmsd.pack.domain.PackageCollectionDetail;
import com.szmsd.pack.dto.PackageCollectionQueryDto;
import com.szmsd.pack.mapper.PackageCollectionMapper;
import com.szmsd.pack.service.IPackageCollectionDetailService;
import com.szmsd.pack.service.IPackageCollectionService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * package - 交货管理 - 揽收 服务实现类
 * </p>
 *
 * @author asd
 * @since 2022-02-17
 */
@Service
public class PackageCollectionServiceImpl extends ServiceImpl<PackageCollectionMapper, PackageCollection> implements IPackageCollectionService {

    @Autowired
    private IPackageCollectionDetailService packageCollectionDetailService;

    /**
     * 查询package - 交货管理 - 揽收模块
     *
     * @param id package - 交货管理 - 揽收模块ID
     * @return package - 交货管理 - 揽收模块
     */
    @Override
    public PackageCollection selectPackageCollectionById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询package - 交货管理 - 揽收模块列表
     *
     * @param packageCollection package - 交货管理 - 揽收模块
     * @return package - 交货管理 - 揽收模块
     */
    @Override
    public List<PackageCollection> selectPackageCollectionList(PackageCollection packageCollection) {
        QueryWrapper<PackageCollection> where = new QueryWrapper<PackageCollection>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增package - 交货管理 - 揽收模块
     *
     * @param packageCollection package - 交货管理 - 揽收模块
     * @return 结果
     */
    @Override
    public int insertPackageCollection(PackageCollection packageCollection) {
        packageCollection.setTotalQty(this.countTotalQty(packageCollection.getDetailList()));
        int insert = baseMapper.insert(packageCollection);
        if (insert > 0) {
            this.saveDetail(packageCollection.getId(), packageCollection.getDetailList());
        }
        return insert;
    }

    /**
     * 修改package - 交货管理 - 揽收模块
     *
     * @param packageCollection package - 交货管理 - 揽收模块
     * @return 结果
     */
    @Override
    public int updatePackageCollection(PackageCollection packageCollection) {
        packageCollection.setTotalQty(this.countTotalQty(packageCollection.getDetailList()));
        int update = baseMapper.updateById(packageCollection);
        if (update > 0) {
            LambdaQueryWrapper<PackageCollectionDetail> packageCollectionDetailLambdaQueryWrapper = Wrappers.lambdaQuery();
            packageCollectionDetailLambdaQueryWrapper.eq(PackageCollectionDetail::getCollectionId, packageCollection.getId());
            this.packageCollectionDetailService.remove(packageCollectionDetailLambdaQueryWrapper);
            this.saveDetail(packageCollection.getId(), packageCollection.getDetailList());
        }
        return update;
    }

    private int countTotalQty(List<PackageCollectionDetail> detailList) {
        int totalQty = 0;
        if (CollectionUtils.isNotEmpty(detailList)) {
            for (PackageCollectionDetail detail : detailList) {
                Integer qty = detail.getQty();
                if (null == qty) {
                    qty = 0;
                }
                totalQty += qty;
            }
        }
        return totalQty;
    }

    private void saveDetail(Long collectionId, List<PackageCollectionDetail> detailList) {
        if (CollectionUtils.isNotEmpty(detailList)) {
            for (PackageCollectionDetail detail : detailList) {
                detail.setCollectionId(collectionId);
            }
            this.packageCollectionDetailService.saveBatch(detailList);
        }
    }

    /**
     * 批量删除package - 交货管理 - 揽收模块
     *
     * @param ids 需要删除的package - 交货管理 - 揽收模块ID
     * @return 结果
     */
    @Override
    public int deletePackageCollectionByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除package - 交货管理 - 揽收模块信息
     *
     * @param id package - 交货管理 - 揽收模块ID
     * @return 结果
     */
    @Override
    public int deletePackageCollectionById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public IPage<PackageCollection> page(PackageCollectionQueryDto dto) {
        IPage<PackageCollection> iPage = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<PackageCollection> queryWrapper = Wrappers.lambdaQuery();
        // 揽收单号
        this.autoSettingListCondition(queryWrapper, PackageCollection::getCollectionNo, this.getTextList(dto.getCollectionNo()));
        // 跟踪号
        this.autoSettingListCondition(queryWrapper, PackageCollection::getTrackingNo, this.getTextList(dto.getTrackingNo()));
        // 创建时间
        this.autoSettingDateCondition(queryWrapper, PackageCollection::getCreateTime, dto.getCreateTimes());
        // 揽收时间
        this.autoSettingDateCondition(queryWrapper, PackageCollection::getCollectionDate, dto.getCollectionTimes());
        // 状态
        queryWrapper.eq(StringUtils.isNotEmpty(dto.getStatus()), PackageCollection::getStatus, dto.getStatus());
        // 揽收人
        queryWrapper.like(StringUtils.isNotEmpty(dto.getCollectionName()), PackageCollection::getCollectionName, dto.getCollectionName());
        // 揽收至仓库
        queryWrapper.eq(StringUtils.isNotEmpty(dto.getCollectionToWarehouse()), PackageCollection::getCollectionToWarehouse, dto.getCollectionToWarehouse());
        // 处理方式
        queryWrapper.eq(StringUtils.isNotEmpty(dto.getHandleMode()), PackageCollection::getHandleMode, dto.getHandleMode());
        IPage<PackageCollection> page = super.page(iPage, queryWrapper);
        List<PackageCollection> records = page.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            List<Long> collectionIdList = records.stream().map(PackageCollection::getId).collect(Collectors.toList());
            Map<Long, String> collectionSkuNames = this.packageCollectionDetailService.getCollectionSkuNames(collectionIdList);
            if (MapUtils.isNotEmpty(collectionSkuNames)) {
                for (PackageCollection record : records) {
                    record.setSkuNames(collectionSkuNames.getOrDefault(record.getId(), "-"));
                }
            }
        }
        return page;
    }

    private void autoSettingDateCondition(LambdaQueryWrapper<PackageCollection> queryWrapper, SFunction<PackageCollection, ?> column, String[] dates) {
        if (ArrayUtils.isNotEmpty(dates) && dates.length == 2) {
            queryWrapper.between(column, dates[0] + " 00:00:00", dates[1] + " 23:59:59");
        }
    }

    private void autoSettingListCondition(LambdaQueryWrapper<PackageCollection> queryWrapper, SFunction<PackageCollection, ?> column, List<?> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() == 1) {
                queryWrapper.eq(PackageCollection::getCollectionNo, list.get(0));
            } else {
                queryWrapper.in(PackageCollection::getCollectionNo, list);
            }
        }
    }

    private List<String> getTextList(String text) {
        if (StringUtils.isNotEmpty(text)) {
            List<String> textList;
            if (text.contains("\n") || text.contains("\r")) {
                if (text.contains("\r")) {
                    text = text.replaceAll("\r\n", "\n");
                    text = text.replaceAll("\r", "\n");
                }
                String[] textArray = text.split("\n");
                textList = Arrays.stream(textArray).collect(Collectors.toList());
            } else {
                textList = Collections.singletonList(text);
            }
            return textList;
        } else {
            return null;
        }
    }
}

