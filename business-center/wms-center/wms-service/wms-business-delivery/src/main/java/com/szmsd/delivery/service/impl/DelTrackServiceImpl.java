package com.szmsd.delivery.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.api.feign.BasCarrierKeywordFeignService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.StringToolkit;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelTrack;
import com.szmsd.delivery.dto.TrackAnalysisDto;
import com.szmsd.delivery.dto.TrackAnalysisExportDto;
import com.szmsd.delivery.dto.TrackAnalysisRequestDto;
import com.szmsd.delivery.dto.TrackingYeeTraceDto;
import com.szmsd.delivery.event.ChangeDelOutboundLatestTrackEvent;
import com.szmsd.delivery.mapper.DelOutboundMapper;
import com.szmsd.delivery.mapper.DelTrackMapper;
import com.szmsd.delivery.service.IDelTrackRemarkService;
import com.szmsd.delivery.service.IDelTrackService;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.PricedProductInServiceCriteria;
import com.szmsd.http.vo.PricedProduct;
import com.szmsd.pack.api.feign.PackageCollectionFeignService;
import com.szmsd.pack.constant.PackageConstant;
import com.szmsd.pack.domain.PackageCollection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author YM
 * @since 2022-02-10
 */
@Service
@Slf4j
public class DelTrackServiceImpl extends ServiceImpl<DelTrackMapper, DelTrack> implements IDelTrackService {

    @Autowired
    private DelOutboundMapper delOutboundMapper;

    @Autowired
    private BasCarrierKeywordFeignService basCarrierKeywordFeignService;

    @Autowired
    private BasSubClientService basSubClientService;

    @Autowired
    private IHtpPricedProductClientService htpPricedProductClientService;

    @Autowired
    private PackageCollectionFeignService packageCollectionFeignService;

    @Autowired
    private IDelTrackRemarkService trackRemarkService;

    @Resource
    private ApplicationContext applicationContext;
    @Autowired
    private RedisTemplate redisTemplate;



    @Override
    public void addData(DelTrack track) {
        track.setSource("2");
        track.setTrackingTime(new Date());
        applicationContext.publishEvent(new ChangeDelOutboundLatestTrackEvent(track));
        baseMapper.insert(track);
    }

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public DelTrack selectDelTrackById(String id) {
        return baseMapper.selectById(id);
    }


    /**
     * 查询模块列表
     *
     * @param delTrack 模块
     * @return 模块
     */
    @Override
    public List<DelTrack> commonTrackList(List<String> orderNos) {
        LambdaQueryWrapper<DelTrack> delTrackLambdaQueryWrapper = Wrappers.lambdaQuery();
        delTrackLambdaQueryWrapper.in(DelTrack::getOrderNo, orderNos);
        delTrackLambdaQueryWrapper.or();
        delTrackLambdaQueryWrapper.in(DelTrack::getTrackingNo, orderNos);
        delTrackLambdaQueryWrapper.orderByDesc(DelTrack::getTrackingTime);
        List<DelTrack> selectList = baseMapper.selectList(delTrackLambdaQueryWrapper);
        return selectList;
    }

    /**
     * 查询模块列表
     *
     * @param delTrack 模块
     * @return 模块
     */
    @Override
    public List<DelTrack> selectDelTrackList(DelTrack delTrack) {
        LambdaQueryWrapper<DelTrack> delTrackLambdaQueryWrapper = Wrappers.lambdaQuery();
        boolean orderNoNotEmpty = StringUtils.isNotEmpty(delTrack.getOrderNo());
        delTrackLambdaQueryWrapper.eq(orderNoNotEmpty, DelTrack::getOrderNo, delTrack.getOrderNo());
        delTrackLambdaQueryWrapper.eq(StringUtils.isNotBlank(delTrack.getSource()), DelTrack::getSource, delTrack.getSource());
        delTrackLambdaQueryWrapper
                .ge(StringUtils.isNotBlank(delTrack.getBeginTime()), BaseEntity::getCreateTime, delTrack.getBeginTime())
                .le(StringUtils.isNotBlank(delTrack.getEndTime()), BaseEntity::getCreateTime, delTrack.getEndTime())
                .eq(StringUtils.isNotBlank(delTrack.getTrackingNo()), DelTrack::getTrackingNo, delTrack.getTrackingNo())
                .eq(StringUtils.isNotBlank(delTrack.getCreateByName()), DelTrack::getCreateByName, delTrack.getCreateByName())
                .orderByDesc(DelTrack::getTrackingTime)
        ;
        List<DelTrack> selectList = baseMapper.selectList(delTrackLambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(selectList) && orderNoNotEmpty) {
            String carrierCode = "";
            if ("DEL".equals(delTrack.getSourceType())) {
                LambdaQueryWrapper<DelOutbound> delOutboundLambdaQueryWrapper = Wrappers.lambdaQuery();
                delOutboundLambdaQueryWrapper.eq(DelOutbound::getOrderNo, delTrack.getOrderNo());
                DelOutbound delOutbound = delOutboundMapper.selectOne(delOutboundLambdaQueryWrapper);
                if (null != delOutbound) {
                    carrierCode = delOutbound.getLogisticsProviderCode();
                }
            } else if ("PCK".equals(delTrack.getSourceType())) {
                PackageCollection queryPackageCollection = new PackageCollection();
                queryPackageCollection.setCollectionNo(delTrack.getOrderNo());
                queryPackageCollection.setHasDetail("N");
                R<PackageCollection> packageCollectionR = packageCollectionFeignService.getInfoByNo(queryPackageCollection);
                if (null != packageCollectionR) {
                    PackageCollection packageCollection = packageCollectionR.getData();
                    carrierCode = packageCollection.getLogisticsProviderCode();
                }
            }
            // default Y
            String filterKeyword = delTrack.getFilterKeyword();
            if (StringUtils.isEmpty(filterKeyword)) {
                filterKeyword = "Y";
            }
            // filter keyword value is 'Y' and carrier code is not empty
            if ("Y".equals(filterKeyword) && StringUtils.isNotEmpty(carrierCode)) {
                for (int i = 0; i < selectList.size(); i++) {
                    DelTrack track = selectList.get(i);
                    // check
                    R<Boolean> booleanR = this.basCarrierKeywordFeignService.checkExistKeyword(carrierCode, track.getDisplay());
                    boolean ignore;
                    if (null != booleanR) {
                        Boolean data = booleanR.getData();
                        if (null != data) {
                            // check result value
                            ignore = data;
                        } else {
                            // check result value is null, default ignore
                            ignore = true;
                        }
                    } else {
                        // check result is null, ignore
                        ignore = true;
                    }
                    if (ignore) {
                        selectList.remove(i);
                        i--;
                    }
                }
            }
        }
        return selectList;
    }

    /**
     * 新增模块
     *
     * @param delTrack 模块
     * @return 结果
     */
    @Override
    public int insertDelTrack(DelTrack delTrack) {
        return baseMapper.insert(delTrack);
    }

    /**
     * 修改模块
     *
     * @param delTrack 模块
     * @return 结果
     */
    @Override
    public int updateDelTrack(DelTrack delTrack) {
        return baseMapper.updateById(delTrack);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteDelTrackByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteDelTrackById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void traceCallback(TrackingYeeTraceDto trackingYeeTraceDto) {
        log.info("接收到TrackingYee回调参数：{}", JSON.toJSONString(trackingYeeTraceDto));
        List<TrackingYeeTraceDto.LogisticsTrackingSectionsDto> logisticsTrackingSections = trackingYeeTraceDto.getLogisticsTrackingSections();
        if (CollectionUtils.isEmpty(logisticsTrackingSections)) {
            return;
        }
        List<DelTrack> trackList = new ArrayList<>();
        logisticsTrackingSections.forEach(trackingSection -> {
            TrackingYeeTraceDto.LogisticsTrackingDto logisticsTracking = trackingSection.getLogisticsTracking();
            // 只获取主运单号的轨迹  关联运单号的暂不获取
            if (logisticsTracking != null && logisticsTracking.getTrackingNo().equalsIgnoreCase(trackingYeeTraceDto.getTrackingNo())) {
                List<TrackingYeeTraceDto.ItemsDto> trackingItems = logisticsTracking.getItems();
                trackingItems.forEach(item -> {
                    // 获取时间
                    Date trackingTime = null;
                    TrackingYeeTraceDto.TrackingTimeDto trackingTimeDto = item.getTrackingTime();
                    if (trackingTimeDto != null) {
                        String trackingTimeStr = trackingTimeDto.getUtcTime();
                        if (StringUtils.isNotBlank(trackingTimeStr)) {
                            // UTC Date
                            trackingTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, trackingTimeStr.replace("T", " ").replace("Z", ""));
                        }else {
                            // Normal Date
                            trackingTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, trackingTimeDto.getDateTime());
                        }
                    }

                    // 校验路由信息存不存在
                    Integer trackCount = this.count(new LambdaQueryWrapper<DelTrack>().eq(DelTrack::getOrderNo, trackingYeeTraceDto.getOrderNo())
                            .eq(DelTrack::getTrackingNo, trackingYeeTraceDto.getTrackingNo())
//                            .eq(DelTrack::getTrackingTime, trackingTime)
                                    .eq(DelTrack::getNo,item.getNo())
                    );
                    if (trackCount == 0) {

                        Map maps=new HashMap();
                        maps.put("carrierCode",trackingYeeTraceDto.getCarrierCode());
                        DelTrack delTrack = new DelTrack();
                        delTrack.setTrackingNo(trackingYeeTraceDto.getTrackingNo());
                        delTrack.setCarrierCode(trackingYeeTraceDto.getCarrierCode());
                        delTrack.setShipmentId(trackingYeeTraceDto.getShipmentId());
                        delTrack.setOrderNo(trackingYeeTraceDto.getOrderNo());
                        delTrack.setTrackingStatus(logisticsTracking.getStatus());
                        delTrack.setNo(item.getNo());
                        maps.put("carrierKeywordType","description");
                        maps.put("originaKeywords",item.getDescription());
                        Map CarrierKeywordMaps = basCarrierKeywordFeignService.selectCarrierKeyword(maps).getData();
                        if (CarrierKeywordMaps==null){
                            delTrack.setDescription(item.getDescription());
                            delTrack.setDmDescription(item.getDescription());
                        }
                        if (CarrierKeywordMaps!=null){
                            delTrack.setDescription(String.valueOf(CarrierKeywordMaps.get("nowKeywords")));
                            delTrack.setDmDescription(item.getDescription());
                        }

                        delTrack.setTrackingTime(trackingTime);
                        delTrack.setActionCode(item.getActionCode());
                        // 获取地址
                        TrackingYeeTraceDto.LocationDto itemLocation = item.getLocation();
                        if (itemLocation != null) {
                            maps.put("carrierKeywordType","display");
                            maps.put("originaKeywords",itemLocation.getDisplay());

                            Map CarrierKeywordMap = basCarrierKeywordFeignService.selectCarrierKeyword(maps).getData();
                            if (CarrierKeywordMap==null){
                                delTrack.setDisplay(itemLocation.getDisplay());
                                delTrack.setDmDisplay(itemLocation.getDisplay());
                            }
                            if (CarrierKeywordMap!=null){
                                delTrack.setDisplay(String.valueOf(CarrierKeywordMap.get("nowKeywords")));
                                delTrack.setDmDisplay(itemLocation.getDisplay());
                            }

                            TrackingYeeTraceDto.AddressDto address = itemLocation.getAddress();
                            if (address != null) {
                                TrackingYeeTraceDto.CountryDto countryDto = address.getCountry();
                                if (countryDto != null) {
                                    delTrack.setCountryCode(countryDto.getAlpha2Code());
                                    delTrack.setCountryNameEn(countryDto.getEnName());
                                    delTrack.setCountryNameCn(countryDto.getCnName());
                                }
                                delTrack.setProvince(address.getProvince());
                                delTrack.setCity(address.getCity());
                                delTrack.setPostcode(address.getPostcode());
                                delTrack.setStreet1(address.getStreet1());
                                delTrack.setStreet2(address.getStreet2());
                                delTrack.setStreet3(address.getStreet3());
                            }
                        }
                        trackList.add(delTrack);
                    }
                });
            }
        });
        if (CollectionUtils.isNotEmpty(trackList)) {
            this.saveBatch(trackList);
            if (StringUtils.isBlank(trackingYeeTraceDto.getOrderNo())) {
                return;
            }
            // 如果LS开头的单号则为揽收单 修改揽收单的状态
            if (trackingYeeTraceDto.getOrderNo().startsWith(PackageConstant.LS_PREFIX)) {
                // 已妥投修改揽收单状态为完成
                if ("Delivered".equalsIgnoreCase(trackingYeeTraceDto.getTrackingStatus())) {
                    packageCollectionFeignService.updateCollectingCompleted(trackingYeeTraceDto.getOrderNo());
                } else {
                    packageCollectionFeignService.updateCollecting(trackingYeeTraceDto.getOrderNo());
                }
            } else {
                DelOutbound delOutbound = delOutboundMapper.selectOne(new LambdaQueryWrapper<DelOutbound>().eq(DelOutbound::getOrderNo, trackingYeeTraceDto.getOrderNo()).last("limit 1"));
                if (delOutbound != null) {
                    List<DelTrack> delTrackList = trackList.stream().sorted(Comparator.comparing(DelTrack::getNo).reversed()).collect(Collectors.toList());
                    DelTrack delTrack = delTrackList.get(0);
                    DelOutbound updateDelOutbound = new DelOutbound();
                    updateDelOutbound.setId(delOutbound.getId());
                    updateDelOutbound.setTrackingStatus(trackingYeeTraceDto.getTrackingStatus());
                    // 最新时间
                    Date latestDate = trackList.stream().map(DelTrack::getTrackingTime).max((d1, d2) -> d1.compareTo(d2)).orElse(null);
                    updateDelOutbound.setTrackingTime(latestDate);
                    updateDelOutbound.setTrackingDescription(delTrack.getDescription() + " (" + DateUtil.format(delTrack.getTrackingTime(), DateUtils.YYYY_MM_DD_HH_MM_SS) + ")");
                    delOutboundMapper.updateById(updateDelOutbound);
                }
            }
        }
    }



    @Override
    public List<TrackAnalysisDto> getTrackAnalysis(TrackAnalysisRequestDto requestDto) {
        List<TrackAnalysisDto> trackAnalysis = baseMapper.getTrackAnalysis(queryWrapper(requestDto).eq(StringUtils.isNotBlank(requestDto.getCountryCode()), "b.country_code", requestDto.getCountryCode()));
        List<TrackAnalysisDto> trackAnalysisResult = new ArrayList<>();
        Map<String, String> subList = basSubClientService.getSubListByLang("099", requestDto.getLang()); // 099为轨迹状态
        subList.forEach((k, v) -> {
            TrackAnalysisDto analysisDto = new TrackAnalysisDto();
            analysisDto.setKeyName(k);
            analysisDto.setKeyCode(v);
            TrackAnalysisDto trackAnalysisDto = trackAnalysis.stream().filter(a -> a.getKeyCode().equalsIgnoreCase(v)).findFirst().orElse(null);
            if (trackAnalysisDto != null) {
                analysisDto.setNum(trackAnalysisDto.getNum());
            } else {
                analysisDto.setNum(0);
            }
            trackAnalysisResult.add(analysisDto);
        });
        return trackAnalysisResult;
    }

    @Override
    public List<TrackAnalysisDto> getProductServiceAnalysis(TrackAnalysisRequestDto requestDto) {
        PricedProductInServiceCriteria serviceCriteria = new PricedProductInServiceCriteria();
        if (StringUtils.isNotBlank(requestDto.getCountryName())) {
            serviceCriteria.setCountryName(requestDto.getCountryName());
        }
        List<TrackAnalysisDto> trackAnalysisResult = new ArrayList<>();
        List<PricedProduct> products = htpPricedProductClientService.inService(serviceCriteria);
        List<TrackAnalysisDto> serviceAnalysis = baseMapper.getProductServiceAnalysis(queryWrapper(requestDto).eq(StringUtils.isNotBlank(requestDto.getCountryCode()), "b.country_code", requestDto.getCountryCode()));
        products.forEach(p -> {
            TrackAnalysisDto analysisDto = new TrackAnalysisDto();
            analysisDto.setKeyName(p.getName());
            analysisDto.setKeyCode(p.getCode());
            TrackAnalysisDto trackAnalysisDto = serviceAnalysis.stream().filter(a -> a.getKeyCode().equalsIgnoreCase(p.getCode())).findFirst().orElse(null);
            if (trackAnalysisDto != null) {
                analysisDto.setNum(trackAnalysisDto.getNum());
            } else {
                analysisDto.setNum(0);
            }
            trackAnalysisResult.add(analysisDto);
        });
        return trackAnalysisResult;
    }

    @Override
    public List<TrackAnalysisExportDto> getAnalysisExportData(TrackAnalysisRequestDto requestDto) {
        PricedProductInServiceCriteria serviceCriteria = new PricedProductInServiceCriteria();
        if (StringUtils.isNotBlank(requestDto.getCountryName())) {
            serviceCriteria.setCountryName(requestDto.getCountryName());
        }
        List<TrackAnalysisDto> trackAnalysisResult = new ArrayList<>();
        List<PricedProduct> products = htpPricedProductClientService.inService(serviceCriteria);
        Map<String, String> subList = basSubClientService.getSubListByLang("099", requestDto.getLang());

        QueryWrapper<TrackAnalysisRequestDto> wrapper = queryWrapper(requestDto)
                .ne("a.order_no", "");
        if (requestDto.getDateType() != null && requestDto.getDateType() == 3) {
            wrapper.ge(StringUtils.isNotBlank(requestDto.getStartTime()), "b.tracking_time", DateUtils.parseDate(requestDto.getStartTime()));
            wrapper.le(StringUtils.isNotBlank(requestDto.getEndTime()), "b.tracking_time", DateUtils.parseDate(requestDto.getEndTime()));
        }
        wrapper.eq(StringUtils.isNotBlank(requestDto.getCountryCode()), "c.country_code", requestDto.getCountryCode());
        List<TrackAnalysisExportDto> exportData = baseMapper.getAnalysisExportData(wrapper);
        Date now = new Date();
        exportData.forEach(data -> {
            // 发货天数（导出当天-发货时间的天数）、轨迹天数（导出当天-最新轨迹时间的天数）
            Date shipmentsTime = data.getShipmentsTime();
            if (shipmentsTime != null) {
                data.setShipmentsDays(DateUtil.betweenDay(shipmentsTime, now, true));
            }

            Date latestTrackTime = data.getLatestTrackTime();
            if (latestTrackTime != null) {
                data.setTrackDays(DateUtil.betweenDay(latestTrackTime,now,  true));
            }

            // 设置轨迹备注  直接读取redis缓存
            Object remarkObj = redisTemplate.opsForHash().get(DelTrackRemarkServiceImpl.TRACK_REMARK_KEY, data.getLatestTrackInfo());
            if (remarkObj != null) {
                data.setTrackRemark((String)remarkObj);
            }

            // 设置物流状态中文
            subList.forEach((k, v) -> {
                if (v.equalsIgnoreCase(data.getTrackingStatus())) {
                    data.setTrackingStatus(k);
                    return;
                }
            });
            PricedProduct product = products.stream().filter(v -> v.getCode().equalsIgnoreCase(data.getShipmentRule())).findFirst().orElse(null);
            if (product != null) {
                data.setShipmentRuleName(product.getName());
            }
        });
        return exportData;
    }

    private QueryWrapper<TrackAnalysisRequestDto> queryWrapper(TrackAnalysisRequestDto requestDto) {
        QueryWrapper<TrackAnalysisRequestDto> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(requestDto.getShipmentService()), "a.shipment_rule", requestDto.getShipmentService());
//        wrapper.eq(StringUtils.isNotBlank(requestDto.getCountryCode()), "b.country_code", requestDto.getCountryCode());
        wrapper.eq(StringUtils.isNotBlank(requestDto.getWarehouseCode()), "a.warehouse_code", requestDto.getWarehouseCode());
        wrapper.eq(StringUtils.isNotBlank(requestDto.getTrackingStatus()), "a.tracking_status", requestDto.getTrackingStatus());
        if (requestDto.getDateType() != null) {
            if (requestDto.getDateType() == 1) {
                wrapper.ge(StringUtils.isNotBlank(requestDto.getStartTime()), "a.create_time", DateUtils.parseDate(requestDto.getStartTime()));
                wrapper.le(StringUtils.isNotBlank(requestDto.getEndTime()), "a.create_time", DateUtils.parseDate(requestDto.getEndTime()));
            } else if (requestDto.getDateType() == 2) {
                wrapper.ge(StringUtils.isNotBlank(requestDto.getStartTime()), "a.shipments_time", DateUtils.parseDate(requestDto.getStartTime()));
                wrapper.le(StringUtils.isNotBlank(requestDto.getEndTime()), "a.shipments_time", DateUtils.parseDate(requestDto.getEndTime()));
            }
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null != loginUser) {
            wrapper.eq("a.seller_code", loginUser.getSellerCode());
        }
        return wrapper;
    }

}

