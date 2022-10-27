package com.szmsd.exception.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.dto.AttachmentDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.datascope.annotation.DataScope;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.api.service.DelOutboundClientService;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.dto.DelOutboundAddressDto;
import com.szmsd.delivery.dto.DelOutboundAgainTrackingNoDto;
import com.szmsd.delivery.dto.DelOutboundFurtherHandlerDto;
import com.szmsd.delivery.vo.DelOutboundListExceptionMessageExportVO;
import com.szmsd.delivery.vo.DelOutboundListExceptionMessageVO;
import com.szmsd.exception.domain.ExceptionInfo;
import com.szmsd.exception.dto.*;
import com.szmsd.exception.enums.ExceptionTypeEnum;
import com.szmsd.exception.enums.OrderTypeEnum;
import com.szmsd.exception.enums.ProcessTypeEnum;
import com.szmsd.exception.enums.StateSubEnum;
import com.szmsd.exception.mapper.ExceptionInfoMapper;
import com.szmsd.exception.service.IExceptionInfoService;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.http.api.feign.HtpExceptionFeignService;
import com.szmsd.http.dto.ExceptionProcessRequest;
import com.szmsd.http.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author l
 * @since 2021-03-30
 */
@Service
@Slf4j
public class ExceptionInfoServiceImpl extends ServiceImpl<ExceptionInfoMapper, ExceptionInfo> implements IExceptionInfoService {

    @Resource
    private HtpExceptionFeignService htpExceptionFeignService;

    @SuppressWarnings({"all"})
    @Autowired
    private RemoteAttachmentService remoteAttachmentService;

    @Autowired
    private DelOutboundClientService delOutboundClientService;

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public ExceptionInfo selectExceptionInfoById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param exceptionInfo 模块
     * @return 模块
     */
    @Override
    public List<ExceptionInfo> selectExceptionInfoList(ExceptionInfo exceptionInfo) {
        QueryWrapper<ExceptionInfo> where = new QueryWrapper<ExceptionInfo>();
        return baseMapper.selectList(where);
    }

    @Override
    //@DataScope("seller_code")
    public TableDataInfo<ExceptionInfo> selectExceptionInfoPage(ExceptionInfoQueryDto dto) {

        //客户端
        if (dto.getType()==0) {
            log.info("开始执行selectExceptionInfoPage查询：{}",dto);
            List<ExceptionInfo> exceptionInfoList=new ArrayList<>();
            QueryWrapper<ExceptionInfo> where = new QueryWrapper<ExceptionInfo>();
            if (dto.getSellerCode() != null) {
                List<String> list = Arrays.asList(dto.getSellerCode().split(","));
                dto.setSellerCodes(list);
            }
            this.handlerQueryCondition(where, dto);
            int count=baseMapper.selectCount(where);
            where.orderByDesc("create_time");
            where.last("limit "+(dto.getPageNum()-1)*dto.getPageSize()+","+dto.getPageSize());

            exceptionInfoList = baseMapper.selectList(where);
            if (CollectionUtils.isNotEmpty(exceptionInfoList)) {
                // 查询异常描述信息
                List<String> orderNos = exceptionInfoList.stream().map(ExceptionInfo::getOrderNo).collect(Collectors.toList());
                log.info("开始执行查询DelOutboundListExceptionMessageVO----------");
                List<DelOutboundListExceptionMessageVO> exceptionMessageList = this.delOutboundClientService.exceptionMessageList(orderNos);
                log.info("结束执行查询DelOutboundListExceptionMessageVO----------");
                if (CollectionUtils.isNotEmpty(exceptionInfoList)) {
                    Map<String, String> exceptionMessageMap = exceptionMessageList.stream().collect(Collectors.toMap(DelOutboundListExceptionMessageVO::getOrderNo, DelOutboundListExceptionMessageVO::getExceptionMessage));
                    for (ExceptionInfo exceptionInfo : exceptionInfoList) {
                        exceptionInfo.setExceptionMessage(exceptionMessageMap.get(exceptionInfo.getOrderNo()));
                    }
                }
            }
            TableDataInfo<ExceptionInfo> table = new TableDataInfo(exceptionInfoList,count);
            table.setCode(200);
            return table;
        }else if (dto.getType()==1){
            log.info("开始执行selectExceptionInfoPage查询：{}",dto);
            List<ExceptionInfo> exceptionInfoList=new ArrayList<>();
            //pc端
            LoginUser loginUser = SecurityUtils.getLoginUser();
            List<String> sellerCodeList=null;
            if (null != loginUser && !loginUser.getUsername().equals("admin")) {
                String username = loginUser.getUsername();
                log.info("开始执行sellerCode查询表----------");
                sellerCodeList=baseMapper.selectsellerCode(username);
                log.info("结束执行sellerCode查询表----------：{}",sellerCodeList.size());

                if (sellerCodeList.size()>0){
                    dto.setSellerCodes(sellerCodeList);

                }
                if (sellerCodeList.size()==0){
                    sellerCodeList.add("");
                    dto.setSellerCodes(sellerCodeList);
                }

            }
            if (null != loginUser && loginUser.getUsername().equals("admin")){
                sellerCodeList=baseMapper.selectsellerCodes();
                if (sellerCodeList.size()>0){
                    dto.setSellerCodes(sellerCodeList);

                }
            }

            QueryWrapper<ExceptionInfo> where = new QueryWrapper<ExceptionInfo>();
            if (dto.getSellerCode() != null) {
                List<String> list = Arrays.asList(dto.getSellerCode().split(","));
                dto.setSellerCodes(list);
            }

            this.handlerQueryCondition(where, dto);
            int count=baseMapper.selectCount(where);
            where.orderByDesc("create_time");
            //框架分页失效，手动设置分页值
            where.last("limit "+(dto.getPageNum()-1)*dto.getPageSize()+","+dto.getPageSize());
            log.info("开始执行查询异常表----------");
            exceptionInfoList = baseMapper.selectList(where);
            if (CollectionUtils.isNotEmpty(exceptionInfoList)) {
                // 查询异常描述信息
                List<String> orderNos = exceptionInfoList.stream().map(ExceptionInfo::getOrderNo).collect(Collectors.toList());
                log.info("开始执行查询DelOutboundListExceptionMessageVO----------");
                List<DelOutboundListExceptionMessageVO> exceptionMessageList = this.delOutboundClientService.exceptionMessageList(orderNos);
                log.info("结束执行查询DelOutboundListExceptionMessageVO----------");
                if (CollectionUtils.isNotEmpty(exceptionInfoList)) {
                    Map<String, String> exceptionMessageMap = exceptionMessageList.stream().collect(Collectors.toMap(DelOutboundListExceptionMessageVO::getOrderNo, DelOutboundListExceptionMessageVO::getExceptionMessage));
                    for (ExceptionInfo exceptionInfo : exceptionInfoList) {
                        exceptionInfo.setExceptionMessage(exceptionMessageMap.get(exceptionInfo.getOrderNo()));
                    }
                }
            }
            TableDataInfo<ExceptionInfo> table = new TableDataInfo(exceptionInfoList,count);
            table.setCode(200);
            return table;
        }

//        if (CollectionUtils.isNotEmpty(exceptionInfoList)) {
//            // 查询异常描述信息
//            List<String> orderNos = exceptionInfoList.stream().map(ExceptionInfo::getOrderNo).collect(Collectors.toList());
//            log.info("开始执行查询DelOutboundListExceptionMessageVO----------");
//            List<DelOutboundListExceptionMessageVO> exceptionMessageList = this.delOutboundClientService.exceptionMessageList(orderNos);
//            log.info("结束执行查询DelOutboundListExceptionMessageVO----------");
//            if (CollectionUtils.isNotEmpty(exceptionInfoList)) {
//                Map<String, String> exceptionMessageMap = exceptionMessageList.stream().collect(Collectors.toMap(DelOutboundListExceptionMessageVO::getOrderNo, DelOutboundListExceptionMessageVO::getExceptionMessage));
//                for (ExceptionInfo exceptionInfo : exceptionInfoList) {
//                    exceptionInfo.setExceptionMessage(exceptionMessageMap.get(exceptionInfo.getOrderNo()));
//                }
//            }
//        }
        return null;
    }

    private void handlerQueryCondition(QueryWrapper<ExceptionInfo> where, ExceptionInfoQueryDto dto)  {

        QueryWrapperUtil.filter(where, SqlKeyword.EQ, "exception_type", dto.getExceptionType());
        QueryWrapperUtil.filter(where, SqlKeyword.EQ, "state", dto.getState());
        QueryWrapperUtil.filterDate(where, "create_time", dto.getCreateTimes());
        if (StringUtils.isNotEmpty(dto.getExceptionNos())) {
            String  exceptionNo = null;
            try {
                exceptionNo = URLDecoder.decode(dto.getExceptionNos(),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            List<String> exceptionNoList = splitToArray(exceptionNo, "[\n,]");
            where.in("exception_no", exceptionNoList).or().in("order_no",exceptionNoList);
        }
//        if (CollectionUtils.isNotEmpty(dto.getOrderNos())) {
//            where.in("order_no", dto.getOrderNos());
//        }
        if (CollectionUtils.isNotEmpty(dto.getSellerCodes())){
            where.in("seller_code",dto.getSellerCodes());
        }
    }

    public static List<String> splitToArray(String text, String split) {
        String[] arr = text.split(split);
        if (arr.length == 0) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (String s : arr) {
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            list.add(s);
        }
        return list;
    }

    @Override
//    @DataScope("seller_code")
    public List<ExceptionInfoExportDto> exportList(ExceptionInfoQueryDto dto) {
        QueryWrapper<ExceptionInfo> where = new QueryWrapper<ExceptionInfo>();
        this.handlerQueryCondition(where, dto);
        where.orderByDesc("create_time");
        List<ExceptionInfoExportDto> exportDtoList = baseMapper.exportList(where);
        if (CollectionUtils.isNotEmpty(exportDtoList)) {
            List<String> orderNos = exportDtoList.stream().map(ExceptionInfoExportDto::getOrderNo).collect(Collectors.toList());
            // 查询异常描述信息
            List<DelOutboundListExceptionMessageExportVO> exceptionMessageList = this.delOutboundClientService.exceptionMessageExportList(orderNos);
            if (CollectionUtils.isNotEmpty(exportDtoList)) {
                Map<String, DelOutboundListExceptionMessageExportVO> map = exceptionMessageList.stream().collect(Collectors.toMap(DelOutboundListExceptionMessageExportVO::getOrderNo, v -> v, (a, b) -> a));
                for (ExceptionInfoExportDto exportDto : exportDtoList) {
                    DelOutboundListExceptionMessageExportVO exportVO = map.get(exportDto.getOrderNo());
                    if (null == exportVO) {
                        continue;
                    }
                    exportDto.setExceptionMessage(exportVO.getExceptionMessage());
                    exportDto.setShipmentRule(exportVO.getShipmentRule());
                    exportDto.setConsignee(exportVO.getConsignee());
                    exportDto.setStreet1(exportVO.getStreet1());
                    exportDto.setStreet2(exportVO.getStreet2());
                    exportDto.setCity(exportVO.getCity());
                    exportDto.setStateOrProvince(exportVO.getStateOrProvince());
                    exportDto.setCountry(exportVO.getCountry());
                    exportDto.setPostCode(exportVO.getPostCode());
                    exportDto.setPhoneNo(exportVO.getPhoneNo());
                    exportDto.setEmail(exportVO.getEmail());
                    exportDto.setIoss(exportVO.getIoss());
                }
            }
        }
        return exportDtoList;
    }

    /**
     * 新增模块
     *
     * @param newExceptionRequest 模块
     * @return 结果
     */
    @Override
    public void insertExceptionInfo(NewExceptionRequest newExceptionRequest) {
        String operationOn = newExceptionRequest.getOperateOn();
        newExceptionRequest.setOperateOn(null);
        ExceptionInfo exceptionInfo = BeanMapperUtil.map(newExceptionRequest, ExceptionInfo.class);
        if (StringUtils.isNotEmpty(operationOn)) {
            Date d = dealUTZTime(operationOn);
            exceptionInfo.setOperateOn(d);
        }
        //赋值
        if (ExceptionTypeEnum.get(exceptionInfo.getExceptionType()) == null) {
            throw new BaseException("没有查找到对应异常类型");
        }
        if (OrderTypeEnum.get(exceptionInfo.getOrderType()) == null) {
            throw new BaseException("没有查找到对应订单类型");
        }
        exceptionInfo.setExceptionTypeName(ExceptionTypeEnum.get(exceptionInfo.getExceptionType()).getName());
        exceptionInfo.setOrderTypeName(OrderTypeEnum.get(exceptionInfo.getOrderType()).getName());
        exceptionInfo.setState(StateSubEnum.DAICHULI.getCode());
        baseMapper.insert(exceptionInfo);
    }

    @Override
    public void processExceptionInfo(@RequestBody ProcessExceptionRequest processExceptionRequest) {
        QueryWrapper<ExceptionInfo> queryWrapper = new QueryWrapper();
        queryWrapper.eq("exception_no", processExceptionRequest.getExceptionNo());
        if (super.count(queryWrapper) != 1) {
            throw new BaseException("异常单号不存在");
        }
        String operationOn = processExceptionRequest.getOperateOn();
        processExceptionRequest.setOperateOn(null);
        ExceptionInfo exceptionInfo = BeanMapperUtil.map(processExceptionRequest, ExceptionInfo.class);
        if (StringUtils.isNotEmpty(operationOn)) {
            Date d = dealUTZTime(operationOn);
            exceptionInfo.setOperateOn(d);
        }
        exceptionInfo.setSolveRemark(processExceptionRequest.getRemark());
        exceptionInfo.setState(StateSubEnum.YIWANCHENG.getCode());
        UpdateWrapper<ExceptionInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("exception_no", processExceptionRequest.getExceptionNo());
        super.update(exceptionInfo, updateWrapper);
    }

    @Override
    public void processByOrderNo(@RequestBody ProcessExceptionOrderRequest processExceptionRequest) {
        QueryWrapper<ExceptionInfo> queryWrapper = new QueryWrapper();
        queryWrapper.eq("order_no", processExceptionRequest.getOrderNo());
        if (super.count(queryWrapper) == 0) {
            return;
        }
        String operationOn = processExceptionRequest.getOperateOn();
        processExceptionRequest.setOperateOn(null);
        ExceptionInfo exceptionInfo = BeanMapperUtil.map(processExceptionRequest, ExceptionInfo.class);
        if (StringUtils.isNotEmpty(operationOn)) {
            Date d = dealUTZTime(operationOn);
            exceptionInfo.setOperateOn(d);
        }
        exceptionInfo.setSolveRemark(processExceptionRequest.getRemark());
        exceptionInfo.setState(StateSubEnum.YIWANCHENG.getCode());
        UpdateWrapper<ExceptionInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_no", processExceptionRequest.getOrderNo());
        super.update(exceptionInfo, updateWrapper);
    }

    /**
     * 修改模块
     *
     * @param exceptionInfo 模块
     * @return 结果
     */
//    @Override
//    public int updateExceptionInfo(List<ExceptionInfoDto> exceptionInfo) {
//
//        for (int i=0;i<exceptionInfo.size();i++) {
//            ExceptionInfo exception = super.getById(exceptionInfo.get(i).getId());
//            ExceptionProcessRequest exceptionProcessRequest = BeanMapperUtil.map(exceptionInfo, ExceptionProcessRequest.class);
//            exceptionProcessRequest.setWarehouseCode(exception.getWarehouseCode());
//            exceptionProcessRequest.setExceptionNo(exception.getExceptionNo());
//            if (exceptionInfo.get(i).getProcessType().equals(ProcessTypeEnum.GOONSHIPPING.getCode())) {
//                try {
//                    DelOutboundFurtherHandlerDto delOutboundFurtherHandlerDto = new DelOutboundFurtherHandlerDto();
//                    delOutboundFurtherHandlerDto.setOrderNo(exception.getOrderNo());
//                    delOutboundClientService.furtherHandler(delOutboundFurtherHandlerDto);
//                } catch (Exception e) {
//                    log.info(exception.getOrderNo());
//                }
//            }
//            R<ResponseVO> r = htpExceptionFeignService.processing(exceptionProcessRequest);
//            if (r == null) {
//                throw new BaseException("wms服务调用失败");
//            }
//            if (r.getData() == null) {
//                throw new BaseException("wms服务调用失败");
//            }
//            if (r.getData().getSuccess() == null) {
//                if (r.getData().getErrors() != null) {
//                    throw new BaseException("传wms失败" + r.getData().getErrors());
//                }
//            } else {
//                if (!r.getData().getSuccess()) {
//                    throw new BaseException("传wms失败" + r.getData().getMessage());
//                }
//            }
//            exceptionInfo.get(i).setProcessTypeName(ProcessTypeEnum.get(exceptionInfo.get(i).getProcessType()).getName());
//            exceptionInfo.get(i).setState(StateSubEnum.YICHULI.getCode());
//            if (CollectionUtils.isNotEmpty(exceptionInfo.get(i).getDocumentsFiles())) {
//                AttachmentDTO attachmentDTO = AttachmentDTO.builder().businessNo(exception.getExceptionNo()).businessItemNo(null).fileList(exceptionInfo.get(i).getDocumentsFiles()).attachmentTypeEnum(AttachmentTypeEnum.EXCEPTION_DOCUMENT).build();
//                this.remoteAttachmentService.saveAndUpdate(attachmentDTO);
//            }
//            int a= baseMapper.updateById(exceptionInfo.get(i));
//        }
//        return 1;
//    }

    /**
     * 修改模块
     *
     * @param exceptionInfo 模块
     * @return 结果
     */
    @Override
    public int updateExceptionInfo(ExceptionInfoDto exceptionInfo) {
        ExceptionInfo exception = super.getById(exceptionInfo.getId());
        ExceptionProcessRequest exceptionProcessRequest = BeanMapperUtil.map(exceptionInfo, ExceptionProcessRequest.class);
        exceptionProcessRequest.setWarehouseCode(exception.getWarehouseCode());
        exceptionProcessRequest.setExceptionNo(exception.getExceptionNo());
        if (exceptionInfo.getProcessType().equals(ProcessTypeEnum.GOONSHIPPING.getCode())) {
            try {
                DelOutboundFurtherHandlerDto delOutboundFurtherHandlerDto = new DelOutboundFurtherHandlerDto();
                delOutboundFurtherHandlerDto.setOrderNo(exception.getOrderNo());
                delOutboundClientService.furtherHandler(delOutboundFurtherHandlerDto);
            } catch (Exception e) {
                log.info(exception.getOrderNo());
            }
        }
        R<ResponseVO> r = htpExceptionFeignService.processing(exceptionProcessRequest);
        if (r == null) {
            throw new BaseException("wms服务调用失败");
        }
        if (r.getData() == null) {
            throw new BaseException("wms服务调用失败");
        }
        if (r.getData().getSuccess() == null) {
            if (r.getData().getErrors() != null) {
                throw new BaseException("传wms失败" + r.getData().getErrors());
            }
        } else {
            if (!r.getData().getSuccess()) {
                throw new BaseException("传wms失败" + r.getData().getMessage());
            }
        }
        exceptionInfo.setProcessTypeName(ProcessTypeEnum.get(exceptionInfo.getProcessType()).getName());
        exceptionInfo.setState(StateSubEnum.YICHULI.getCode());
        if (CollectionUtils.isNotEmpty(exceptionInfo.getDocumentsFiles())) {
            AttachmentDTO attachmentDTO = AttachmentDTO.builder().businessNo(exception.getExceptionNo()).businessItemNo(null).fileList(exceptionInfo.getDocumentsFiles()).attachmentTypeEnum(AttachmentTypeEnum.EXCEPTION_DOCUMENT).build();
            this.remoteAttachmentService.saveAndUpdate(attachmentDTO);
        }
        return baseMapper.updateById(exceptionInfo);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteExceptionInfoByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteExceptionInfoById(String id) {
        return baseMapper.deleteById(id);
    }

    @Transactional
    @Override
    public int againTrackingNo(ExceptionDelOutboundAgainTrackingNoDto dto) {
        int i = this.delOutboundClientService.againTrackingNo(dto);
        if (i != 1) {
            throw new CommonException("999", "重新获取挂号失败");
        }
        LambdaUpdateWrapper<ExceptionInfo> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        lambdaUpdateWrapper.set(ExceptionInfo::getState, "085002");
        lambdaUpdateWrapper.eq(ExceptionInfo::getId, dto.getExceptionId());
        return super.baseMapper.update(null, lambdaUpdateWrapper);
    }

    @Transactional
    @Override
    public boolean importAgainTrackingNo(ExceptionInfoExportDto dto, String countryCode) {
        String exceptionNo = dto.getExceptionNo();
        LambdaQueryWrapper<ExceptionInfo> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ExceptionInfo::getExceptionNo, exceptionNo);
        ExceptionInfo exceptionInfo = super.getOne(queryWrapper);
        if (null == exceptionInfo) {
            throw new CommonException("999", "异常单据不存在");
        }
        if ("085001".equals(exceptionInfo.getState())
                && "Shipment".equals(exceptionInfo.getOrderType())
                && ("OutboundGetTrackingFailed".equals(exceptionInfo.getExceptionType())
                || "OutboundOverWeight".equals(exceptionInfo.getExceptionType())
                || "OutboundOverSize".equals(exceptionInfo.getExceptionType()))) {
            DelOutboundAgainTrackingNoDto trackingNoDto = new DelOutboundAgainTrackingNoDto();
            trackingNoDto.setOrderNo(exceptionInfo.getOrderNo());
            trackingNoDto.setShipmentRule(dto.getShipmentRule());
            DelOutboundAddressDto addressDto = new DelOutboundAddressDto();
            addressDto.setConsignee(dto.getConsignee());
            addressDto.setStreet1(dto.getStreet1());
            addressDto.setStreet2(dto.getStreet2());
            addressDto.setCity(dto.getCity());
            addressDto.setStateOrProvince(dto.getStateOrProvince());
            addressDto.setCountryCode(countryCode);
            //汪俊余说Country都传英文名
            addressDto.setCountry(dto.getCountry());
            addressDto.setPostCode(dto.getPostCode());
            addressDto.setPhoneNo(dto.getPhoneNo());
            addressDto.setEmail(dto.getEmail());
            trackingNoDto.setAddress(addressDto);
            trackingNoDto.setDetailList(BeanMapperUtil.mapList(dto.getExceptionInfoDetailExportDtoList(), DelOutboundAddress.class));
            int i = this.delOutboundClientService.againTrackingNo(trackingNoDto);
            if (i != 1) {
                throw new CommonException("999", "重新获取挂号失败");
            }
            LambdaUpdateWrapper<ExceptionInfo> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
            lambdaUpdateWrapper.set(ExceptionInfo::getState, "085002");
            lambdaUpdateWrapper.eq(ExceptionInfo::getId, exceptionInfo.getId());
            return super.update(null, lambdaUpdateWrapper);
        }
        return false;
    }

    @Transactional
    @Override
    public int ignore(ExceptionInfoDto exceptionInfo) {
        // 根据订单号查询异常信息
        String orderNo = exceptionInfo.getOrderNo();
        if (StringUtils.isEmpty(orderNo)) {
            return 0;
        }
        LambdaQueryWrapper<ExceptionInfo> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ExceptionInfo::getOrderNo, orderNo);
        queryWrapper.eq(ExceptionInfo::getState, "085001");
        if (super.count(queryWrapper) > 0) {
            LambdaUpdateWrapper<ExceptionInfo> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.set(ExceptionInfo::getState, "085002");
            updateWrapper.set(ExceptionInfo::getProcessRemark, "系统自动处理");
            updateWrapper.eq(ExceptionInfo::getOrderNo, orderNo);
            updateWrapper.eq(ExceptionInfo::getState, "085001");
            return this.baseMapper.update(null, updateWrapper);
        }
        return 0;
    }

    @Override
    public List<ExceptionInfoDetailExportDto> selectExceptionInfoDetailExport(String orderNo) {
        return baseMapper.selectExceptionInfoDetailExport(orderNo);
    }

    @Override
    public void updateDelOutboundDetail(String orderNo, List<ExceptionInfoDetailExportDto> exceptionInfoDetailExportDtoList) {
        exceptionInfoDetailExportDtoList.forEach(x->{
            baseMapper.updateDelOutboundDetail(x);
        });

    }

    @Override
    public void updateDelOutboundIoss(ExceptionInfoExportDto dto) {
        baseMapper.updateDelOutboundEx(dto);
    }

    private Date dealUTZTime(String time) {
        Date date = new Date();
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}

