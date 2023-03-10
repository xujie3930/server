package com.szmsd.track.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.api.feign.BasCarrierKeywordFeignService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.StringToolkit;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.vo.DelOutboundTrackRequestVO;
import com.szmsd.delivery.vo.DelOutboundVO;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.PricedProductInServiceCriteria;
import com.szmsd.http.vo.PricedProduct;
import com.szmsd.pack.api.feign.PackageCollectionFeignService;
import com.szmsd.pack.constant.PackageConstant;
import com.szmsd.pack.domain.PackageCollection;
import com.szmsd.track.domain.Track;
import com.szmsd.track.dto.*;
import com.szmsd.track.event.ChangeDelOutboundLatestTrackEvent;
import com.szmsd.track.event.TrackTyRequestLogEvent;
import com.szmsd.track.event.EventUtil;
import com.szmsd.track.mapper.TrackMapper;
import com.szmsd.track.service.ITrackService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author YM
 * @since 2022-02-10
 */
@Service
@Slf4j
public class TrackServiceImpl extends ServiceImpl<TrackMapper, Track> implements ITrackService {

    @Autowired
    private DelOutboundFeignService delOutboundFeignService;

    @Autowired
    private BasCarrierKeywordFeignService basCarrierKeywordFeignService;

    @Autowired
    private BasSubClientService basSubClientService;

    @Autowired
    private IHtpPricedProductClientService htpPricedProductClientService;

    @Autowired
    private PackageCollectionFeignService packageCollectionFeignService;

    @Resource
    private ApplicationContext applicationContext;
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void addData(Track track) {
        track.setSource("2");
        track.setTrackingTime(new Date());
        applicationContext.publishEvent(new ChangeDelOutboundLatestTrackEvent(track));
        baseMapper.insert(track);
    }

    /**
     * ????????????
     *
     * @param id ??????ID
     * @return ??????
     */
    @Override
    public Track selectDelTrackById(String id) {
        return baseMapper.selectById(id);
    }


    /**
     * ??????????????????
     * @return ??????
     */
    @Override
    public R<TrackMainCommonDto> commonTrackList(List<String> orderNos) {
        LambdaQueryWrapper<Track> delTrackLambdaQueryWrapper = Wrappers.lambdaQuery();
        delTrackLambdaQueryWrapper.in(Track::getOrderNo, orderNos);
        delTrackLambdaQueryWrapper.or();
        delTrackLambdaQueryWrapper.in(Track::getTrackingNo, orderNos);
        delTrackLambdaQueryWrapper.orderByDesc(Track::getTrackingTime);
        List<Track> list = baseMapper.selectList(delTrackLambdaQueryWrapper);

        if(CollectionUtils.isEmpty(list)){
            return R.failed("?????????");
        }

        Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("099");
        List<BasSubWrapperVO> delTrackStateTypeList = listMap.get("099");
        Map<String, BasSubWrapperVO> delTrackStateTypeMap
                = delTrackStateTypeList.stream().collect(Collectors.toMap(BasSubWrapperVO::getSubValue, Function.identity()));

        Map<String, Boolean> cacheMap = new HashMap<String, Boolean>();
        for (int i = 0; i < list.size(); i++) {
            Track track = list.get(i);
            if(com.szmsd.common.core.utils.StringUtils.isEmpty(track.getCarrierCode())){
                continue;
            }
            boolean ignore = true;
            String key = track.getCarrierCode()+":"+track.getDescription();
            if(!cacheMap.containsKey(key)){
                R<Boolean> booleanR = this.basCarrierKeywordFeignService.checkExistKeyword(track.getCarrierCode(), track.getDisplay());
                if(null != booleanR && booleanR.getData() != null){
                    ignore = booleanR.getData();
                }
                cacheMap.put(key, ignore);
            }else{
                ignore = cacheMap.get(key);
            }
            if (ignore) {
                list.remove(i);
                i--;
            }
        }
        List<TrackCommonDto> newList = BeanMapperUtil.mapList(list, TrackCommonDto.class);
        for (TrackCommonDto dto: newList){
            BasSubWrapperVO vo  = delTrackStateTypeMap.get(dto.getTrackingStatus());
            if (vo != null) {
                dto.setTrackingStatusName(vo.getSubName());
            }
        }


        Set<String> threeSet = new TreeSet<String>();
        for(Track delTrack: list){
            threeSet.add(delTrack.getOrderNo());
        }
        orderNos.clear();
        orderNos.addAll(threeSet);


        //????????????????????????
        Map<String, List<TrackCommonDto>> groupBy = newList.stream().collect(Collectors.groupingBy(TrackCommonDto::getOrderNo));
        Map<String, Integer> delTrackStateDto = new HashMap();
        for (String ordersNo: orderNos){
            List<TrackCommonDto> detailList = groupBy.get(ordersNo);
            if(detailList != null){
                String trackingStatus = detailList.get(0).getTrackingStatus();
                if(delTrackStateDto.containsKey(trackingStatus)){
                    delTrackStateDto.put(trackingStatus, delTrackStateDto.get(trackingStatus) + 1);
                }else{
                    delTrackStateDto.put(trackingStatus, 1);
                }
            }
        }

        //????????????
        List<TrackDetailDto> mainDetailDataList = new ArrayList();
        for (String ordersNo: orderNos){
            List<TrackCommonDto> detailList = groupBy.get(ordersNo);
            if(detailList != null){
                TrackDetailDto detailDto = new TrackDetailDto();
                mainDetailDataList.add(detailDto);
                BeanUtils.copyProperties(detailList.get(0), detailDto);
                detailDto.setTrackingList(detailList);

                //?????????????????????????????????
                long day = 0;
                if(detailDto.getTrackingTime() != null && detailList.get(detailList.size() - 1).getTrackingTime() != null){
                    day = DateUtil.betweenDay(detailDto.getTrackingTime(), detailList.get(detailList.size() - 1).getTrackingTime(),  true);
                    if(day < 0){
                        day = 0;
                    }
                }

                detailDto.setTrackDays(day);

            }
        }

        //??????????????????
        List<String> orders = mainDetailDataList.stream().map(e -> e.getOrderNo()).collect(Collectors.toList());
        if(orders.size() > 0){
            R<List<DelOutboundAddress>> addressRs = delOutboundFeignService.findDelboundAddress(orderNos);

            if(addressRs == null){
                return R.failed("??????????????????");
            }

            if(addressRs.getCode() != Constants.SUCCESS){
                return R.failed(addressRs.getMsg());
            }

            List<DelOutboundAddress> addressList = addressRs.getData();

            Map<String, DelOutboundAddress> addressMap =
                    addressList.stream().collect(Collectors.toMap(DelOutboundAddress::getOrderNo, account -> account));
            for (TrackDetailDto dto: mainDetailDataList){
                DelOutboundAddress address = addressMap.get(dto.getOrderNo());
                if(address != null){
                    BeanUtils.copyProperties(address, dto);
                }
            }
        }

        TrackMainCommonDto mainDto = new TrackMainCommonDto();
        mainDto.setDelTrackStateDto(delTrackStateDto);
        mainDto.setTrackingList(mainDetailDataList);
        mainDto.setDelTrackStateTypeList(delTrackStateTypeList);

        return R.ok(mainDto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateTrack(Track delTrack) {

        if(StringUtils.isEmpty(delTrack.getOrderNo()) || StringUtils.isEmpty(delTrack.getTrackingNo())){
            throw new CommonException("400", "?????????????????????????????????");
        }

        R<DelOutboundVO> delOutboundVOR = delOutboundFeignService.getInfoByOrderNo(delTrack.getOrderNo());

        if(delOutboundVOR == null){
            throw new CommonException("400", "?????????????????????");
        }

        DelOutboundVO delOutboundVO = delOutboundVOR.getData();
        if(!StringUtils.equals(delOutboundVO.getTrackingNo(), delTrack.getTrackingNo())){
            throw new CommonException("400", "??????????????????????????????????????????");
        }

        delTrack.setSource("2"); // ????????????
        delTrack.setTrackingTime(new Date());

        this.saveOrUpdate(delTrack);

        applicationContext.publishEvent(new ChangeDelOutboundLatestTrackEvent(delTrack));

    }

    @Override
    public void pushTY(TrackTyRequestLogDto requestLogDto) {

        EventUtil.publishEvent(new TrackTyRequestLogEvent(requestLogDto));
    }

    @Override
    public R<Integer> checkTrackDoc(String orderNo,Integer trackStayDays) {

        Integer res = 0;

        LambdaQueryWrapper<Track> delTrackLambdaQueryWrapper = Wrappers.lambdaQuery();
        delTrackLambdaQueryWrapper.eq(Track::getOrderNo, orderNo);
        delTrackLambdaQueryWrapper.orderByDesc(Track::getTrackingTime);
        delTrackLambdaQueryWrapper.last("LIMIT 1");
        Track dataDelTrack = baseMapper.selectOne(delTrackLambdaQueryWrapper);
        if (dataDelTrack != null && dataDelTrack.getTrackingTime() != null && DateUtil.betweenDay(dataDelTrack.getTrackingTime(), new Date(), true) <= trackStayDays) {
            res = 1;
        }
        
        return R.ok(res);
    }

    /**
     * ??????????????????
     *
     * @param delTrack ??????
     * @return ??????
     */
    @Override
    public List<Track> selectDelTrackList(Track delTrack) {
        LambdaQueryWrapper<Track> delTrackLambdaQueryWrapper = Wrappers.lambdaQuery();
        String queryNo = delTrack.getQueryNoOne();

        if (StringUtils.isNotEmpty(queryNo)) {
            try {
                queryNo = URLDecoder.decode(delTrack.getQueryNoOne(),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            List<String> queryNoList = StringToolkit.splitToArray(queryNo, "[\n,]");
            delTrackLambdaQueryWrapper.in(Track::getOrderNo, queryNoList)
                    .or().in(Track::getTrackingNo, queryNoList);
        }

        boolean orderNoNotEmpty = StringUtils.isNotEmpty(delTrack.getOrderNo());
        delTrackLambdaQueryWrapper.eq(orderNoNotEmpty, Track::getOrderNo, delTrack.getOrderNo());
        delTrackLambdaQueryWrapper.eq(StringUtils.isNotBlank(delTrack.getSource()), Track::getSource, delTrack.getSource());
        delTrackLambdaQueryWrapper
                .ge(StringUtils.isNotBlank(delTrack.getBeginTime()), BaseEntity::getCreateTime, delTrack.getBeginTime())
                .le(StringUtils.isNotBlank(delTrack.getEndTime()), BaseEntity::getCreateTime, delTrack.getEndTime())
                .eq(StringUtils.isNotBlank(delTrack.getTrackingNo()), Track::getTrackingNo, delTrack.getTrackingNo())
                .eq(StringUtils.isNotBlank(delTrack.getCreateByName()), Track::getCreateByName, delTrack.getCreateByName())
                .orderByDesc(Track::getTrackingTime)
        ;
        List<Track> selectList = baseMapper.selectList(delTrackLambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(selectList) && orderNoNotEmpty) {
            String carrierCode = "";
            if ("DEL".equals(delTrack.getSourceType())) {

                R<DelOutboundVO> delOutboundVOR = delOutboundFeignService.getInfoByOrderNo(delTrack.getOrderNo());

                if(delOutboundVOR == null || delOutboundVOR.getCode() != Constants.SUCCESS){
                    throw new RuntimeException("?????????????????????");
                }

                DelOutboundVO delOutboundVO = delOutboundVOR.getData();
                if (null != delOutboundVO) {
                    carrierCode = delOutboundVO.getLogisticsProviderCode();
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
                    Track track = selectList.get(i);
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
//                    if (ignore) {
//                        selectList.remove(i);
//                        i--;
//                    }
                }
            }
        }
        return selectList;
    }


    /**
     * ????????????
     *
     * @param delTrack ??????
     * @return ??????
     */
    @Override
    public int insertDelTrack(Track delTrack) {
        return baseMapper.insert(delTrack);
    }

    /**
     * ????????????
     *
     * @param delTrack ??????
     * @return ??????
     */
    @Override
    public int updateDelTrack(Track delTrack) {
        return baseMapper.updateById(delTrack);
    }

    /**
     * ??????????????????
     *
     * @param ids ?????????????????????ID
     * @return ??????
     */
    @Override
    public int deleteDelTrackByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * ??????????????????
     *
     * @param id ??????ID
     * @return ??????
     */
    @Override
    public int deleteDelTrackById(String id) {
        return baseMapper.deleteById(id);
    }

//    public static void main(String[] args) {
//        String a="2022-10-18T23:20:00Z";
//        String b="2022-10-19T09:17:00+08:00";
//        String c="2022-12-19 21:18:27";
//        Date trackingTime=DateUtils.parseDate(c);
//        System.out.println(trackingTime);
//    }
    @Override
    @Transactional
    public void traceCallback(TrackingYeeTraceDto trackingYeeTraceDto) {
        log.info("?????????TrackingYee???????????????{}", JSON.toJSONString(trackingYeeTraceDto));
        List<TrackingYeeTraceDto.LogisticsTrackingSectionsDto> logisticsTrackingSections = trackingYeeTraceDto.getLogisticsTrackingSections();
        if (CollectionUtils.isEmpty(logisticsTrackingSections)) {
            return;
        }


        List<Track> trackList = new ArrayList<>();
        logisticsTrackingSections.forEach(trackingSection -> {
            TrackingYeeTraceDto.LogisticsTrackingDto logisticsTracking = trackingSection.getLogisticsTracking();
            // ??????????????????????????????  ??????????????????????????????
            if (logisticsTracking != null && logisticsTracking.getTrackingNo().equalsIgnoreCase(trackingYeeTraceDto.getTrackingNo())) {
                List<TrackingYeeTraceDto.ItemsDto> trackingItems = logisticsTracking.getItems();
                log.info("trackingItems???{}", JSON.toJSONString(trackingItems));
                for (int item=0;item<trackingItems.size();item++){
//                trackingItems.forEach(item -> {
                    log.info("trackingItems???item???{}", JSON.toJSONString(item));
                    // ????????????
                    Date trackingTime = null;
                    TrackingYeeTraceDto.TrackingTimeDto trackingTimeDto = trackingItems.get(item).getTrackingTime();
                    if (trackingTimeDto != null) {
                        String trackingTimeStr = trackingTimeDto.getUtcTime();
                        if (StringUtils.isNotBlank(trackingTimeStr)) {
                            // UTC Date
//                            trackingTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, trackingTimeStr.replace("T", " ").replace("Z", ""));
                            trackingTime=DateUtils.parseDate(trackingTimeStr);
                        }else {
                            // Normal Date
//                            trackingTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, trackingTimeDto.getDateTime());
                            trackingTime=DateUtils.parseDate(trackingTimeDto.getDateTime());
                        }
                    }

                    // ??????????????????????????????
                    Integer trackCount = this.count(new LambdaQueryWrapper<Track>().eq(Track::getOrderNo, trackingYeeTraceDto.getOrderNo())
                            .eq(Track::getTrackingNo, trackingYeeTraceDto.getTrackingNo())
//                            .eq(DelTrack::getTrackingTime, trackingTime)
                                    .eq(Track::getNo, trackingItems.get(item).getNo())
                    );
                    if (trackCount != 0) {
                        Map map=new HashMap();
                        map.put("orderNo", trackingYeeTraceDto.getOrderNo());
                        map.put("trackingNo", trackingYeeTraceDto.getTrackingNo());
                        map.put("no", trackingItems.get(item).getNo());
                        baseMapper.deletetrack(map);
                    }

                        Map maps=new HashMap();
                        maps.put("carrierCode",trackingYeeTraceDto.getCarrierCode());
                        Track delTrack = new Track();
                        delTrack.setTrackingNo(trackingYeeTraceDto.getTrackingNo());
                        delTrack.setCarrierCode(trackingYeeTraceDto.getCarrierCode());
                        delTrack.setShipmentId(trackingYeeTraceDto.getShipmentId());
                        delTrack.setOrderNo(trackingYeeTraceDto.getOrderNo());
                        delTrack.setTrackingStatus(logisticsTracking.getStatus());
                        delTrack.setNo(trackingItems.get(item).getNo());
                        maps.put("carrierKeywordType","description");
                        maps.put("originaKeywords", trackingItems.get(item).getDescription());
                        log.info("DelTrackServiceImpl???????????????????????????description???{}", JSON.toJSONString(maps));
                        Map CarrierKeywordMaps = basCarrierKeywordFeignService.selectCarrierKeyword(maps).getData();
                        log.info("DelTrackServiceImpl?????????????????????????????????description???{}", JSON.toJSONString(CarrierKeywordMaps));
                        if (CarrierKeywordMaps==null){
                            delTrack.setDescription(trackingItems.get(item).getDescription());
                            delTrack.setDmDescription(trackingItems.get(item).getDescription());
                        }
                        if (CarrierKeywordMaps!=null){
                            delTrack.setDescription(String.valueOf(CarrierKeywordMaps.get("nowKeywords")));
                            delTrack.setDmDescription(trackingItems.get(item).getDescription());
                        }

                        delTrack.setTrackingTime(trackingTime);
                        delTrack.setActionCode(trackingItems.get(item).getActionCode());
                        // ????????????
                        TrackingYeeTraceDto.LocationDto itemLocation = trackingItems.get(item).getLocation();
                        if (itemLocation != null) {
                            maps.put("carrierKeywordType","display");
                            maps.put("originaKeywords",itemLocation.getDisplay());
                            log.info("DelTrackServiceImpl???????????????????????????display???{}", JSON.toJSONString(maps));
                            Map CarrierKeywordMap = basCarrierKeywordFeignService.selectCarrierKeyword(maps).getData();
                            log.info("DelTrackServiceImpl?????????????????????????????????display???{}", JSON.toJSONString(CarrierKeywordMap));
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
            }
        });
        log.info("DelTrackServiceImpl???trackList???{}",trackList);
        if (CollectionUtils.isNotEmpty(trackList)) {
            this.saveBatch(trackList);
            if (StringUtils.isBlank(trackingYeeTraceDto.getOrderNo())) {
                return;
            }
            // ??????LS?????????????????????????????? ????????????????????????
            if (trackingYeeTraceDto.getOrderNo().startsWith(PackageConstant.LS_PREFIX)) {
                // ???????????????????????????????????????
                if ("Delivered".equalsIgnoreCase(trackingYeeTraceDto.getTrackingStatus())) {
                    packageCollectionFeignService.updateCollectingCompleted(trackingYeeTraceDto.getOrderNo());
                } else {
                    packageCollectionFeignService.updateCollecting(trackingYeeTraceDto.getOrderNo());
                }
            } else {
                R<DelOutboundVO> delOutboundVOR = delOutboundFeignService.getInfoByOrderNo(trackingYeeTraceDto.getOrderNo());

                if(delOutboundVOR == null || delOutboundVOR.getCode() != Constants.SUCCESS){
                    throw new RuntimeException("?????????????????????");
                }

                DelOutboundVO delOutboundVO = delOutboundVOR.getData();

                if (delOutboundVO != null) {

                    List<Track> delTrackList = trackList.stream().sorted(Comparator.comparing(Track::getTrackingTime).reversed()).collect(Collectors.toList());
                    Track delTrack = delTrackList.get(0);
                    DelOutboundTrackRequestVO delOutboundTrackRequestVO = new DelOutboundTrackRequestVO();
                    delOutboundTrackRequestVO.setTrackingStatus(trackingYeeTraceDto.getTrackingStatus());
                    delOutboundTrackRequestVO.setOrderNo(delOutboundVO.getOrderNo());
                    // ????????????
                    Date latestDate = trackList.stream().map(Track::getTrackingTime).max((d1, d2) -> d1.compareTo(d2)).orElse(null);
                    delOutboundTrackRequestVO.setTrackingTime(latestDate);
                    delOutboundTrackRequestVO.setTrackingDescription(delTrack.getDescription() + " (" + DateUtil.format(delTrack.getTrackingTime(), DateUtils.YYYY_MM_DD_HH_MM_SS) + ")");


                   if (delTrack.getTrackingStatus().equals("Delivered")){
                       Date deliveredDime = trackList.stream().map(Track::getTrackingTime).max((d1, d2) -> d1.compareTo(d2)).orElse(null);
                      Date shipmentsTime =delOutboundVO.getShipmentsTime();
                      if (deliveredDime!=null&&shipmentsTime!=null){
                          long timeDifference=(deliveredDime.getTime()-shipmentsTime.getTime())/(24*60*60*1000);
                          delOutboundTrackRequestVO.setDeliveredDime(deliveredDime);
                          delOutboundTrackRequestVO.setTimeDifference(Integer.parseInt(String.valueOf(timeDifference)));
                      }
                   }

                    delOutboundFeignService.updateDeloutboundTrackMsg(delOutboundTrackRequestVO);

                }
            }
        }
    }



    @Override
    public List<TrackAnalysisDto> getTrackAnalysis(TrackAnalysisRequestDto requestDto) {
        List<TrackAnalysisDto> trackAnalysis = baseMapper.getTrackAnalysis(queryWrapper(requestDto).eq(StringUtils.isNotBlank(requestDto.getCountryCode()), "b.country_code", requestDto.getCountryCode()));

        if(CollectionUtils.isEmpty(trackAnalysis)){
            return new ArrayList<>();
        }

        List<TrackAnalysisDto> trackAnalysisResult = new ArrayList<>();
        Map<String, String> subList = basSubClientService.getSubListByLang("099", requestDto.getLang()); // 099???????????????

        log.info("trackAnalysis??????:{}",JSON.toJSONString(trackAnalysis));

        if(subList == null){
            throw new RuntimeException("099????????????????????????");
        }

        subList.forEach((k, v) -> {
            TrackAnalysisDto analysisDto = new TrackAnalysisDto();
            analysisDto.setKeyName(k);
            analysisDto.setKeyCode(v);
            TrackAnalysisDto trackAnalysisDto = trackAnalysis.stream().filter(a -> v.equalsIgnoreCase(a.getKeyCode())).findFirst().orElse(null);
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
            // ???????????????????????????-??????????????????????????????????????????????????????-??????????????????????????????
            Date shipmentsTime = data.getShipmentsTime();
            if (shipmentsTime != null) {
                data.setShipmentsDays(DateUtil.betweenDay(shipmentsTime, now, true));
            }

            Date latestTrackTime = data.getLatestTrackTime();
            if (latestTrackTime != null) {
                data.setTrackDays(DateUtil.betweenDay(latestTrackTime,now,  true));
            }

            // ??????????????????  ????????????redis??????
            Object remarkObj = redisTemplate.opsForHash().get(TrackRemarkServiceImpl.TRACK_REMARK_KEY, data.getLatestTrackInfo());
            if (remarkObj != null) {
                data.setTrackRemark((String)remarkObj);
            }

            // ????????????????????????
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

