package com.szmsd.delivery.command;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.convert.ChargeImportConvert;
import com.szmsd.delivery.domain.ChargeImport;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.ChargeExcelDto;
import com.szmsd.delivery.enums.ChargeImportStateEnum;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.mapper.DelOutboundMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ChargeReadExcelCmd extends BasicCommand<List<ChargeImport>> {

    private MultipartFile file;

    private static final int BATCH_NUM = 300;

    public ChargeReadExcelCmd(MultipartFile file){
        this.file = file;
    }

    @Override
    protected void beforeDoExecute() {

        AssertUtil.notNull(file, "上传文件不存在");
        String originalFilename = file.getOriginalFilename();
        AssertUtil.notNull(originalFilename, "导入文件名称不存在");
        int lastIndexOf = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(lastIndexOf + 1);
        boolean isXlsx = "xlsx".equals(suffix);
        AssertUtil.isTrue(isXlsx, "请上传xlsx文件");
    }

    @Override
    protected List<ChargeImport> doExecute() throws Exception {

        ExcelReaderSheetBuilder excelReaderSheetBuilder = EasyExcelFactory.read(file.getInputStream(), ChargeExcelDto.class, null).sheet(0);
        List<ChargeExcelDto> chargeExcelDtos = excelReaderSheetBuilder.doReadSync();

        for(ChargeExcelDto excelDto :chargeExcelDtos){

            if(StringUtils.isEmpty(excelDto.getOrderNo())){
                throw new RuntimeException("订单号不允许为空");
            }
        }

        if (CollectionUtils.isEmpty(chargeExcelDtos)) {
            throw new RuntimeException("导入的二次计费数据不能为空");
        }

        //List<String> orderNos = chargeExcelDtos.stream().map(ChargeExcelDto::getOrderNo).collect(Collectors.toList());

        List<ChargeImport> chargeImportList = ChargeImportConvert.INSTANCE.toChargeImportList(chargeExcelDtos);

        String userName = SecurityUtils.getUsername();
        for(ChargeImport chargeImport : chargeImportList){

            String specifications = chargeImport.getSpecifications();
            if(StringUtils.isNotEmpty(specifications)){
                String s[] = specifications.split("\\*");

                if(s.length < 3){
                    throw new RuntimeException(chargeImport.getOrderNo()+"单号，尺寸规则异常");
                }

                chargeImport.setLength(toDecimal(s[0]));
                chargeImport.setWidth(toDecimal(s[1]));
                chargeImport.setHeight(toDecimal(s[2]));
            }

            chargeImport.setCreateBy(userName);
            chargeImport.setCreateTime(new Date());
            chargeImport.setState(ChargeImportStateEnum.INIT.getCode());
            chargeImport.setDelFlag(0);
        }

        List<String> orderNoList = chargeImportList.stream().map(ChargeImport::getOrderNo).distinct().collect(Collectors.toList());

        List<List<String>> parttionList = Lists.partition(orderNoList,BATCH_NUM);
        DelOutboundMapper delOutboundMapper = SpringUtils.getBean(DelOutboundMapper.class);
        List<DelOutbound> resultDelOutbound = new ArrayList<>();

        for(List<String> strings : parttionList) {

            List<DelOutbound> delOutboundList = delOutboundMapper.selectList(Wrappers.<DelOutbound>query().lambda().in(DelOutbound::getOrderNo,strings));
            resultDelOutbound.addAll(delOutboundList);
        }
        //求差集
        List<String> resultDelOrderNoList = resultDelOutbound.stream().map(DelOutbound::getOrderNo).collect(Collectors.toList());
        orderNoList.removeAll(resultDelOrderNoList);

        if(CollectionUtils.isNotEmpty(orderNoList)){
            throw new RuntimeException("订单号["+ JSON.toJSONString(orderNoList)+"]不存在,无法导入");
        }

        List<String> comOrderNoList = new ArrayList<>();
        for(DelOutbound delOutbound : resultDelOutbound){
            if(!DelOutboundStateEnum.COMPLETED.getCode().equals(delOutbound.getState())){
                comOrderNoList.add(delOutbound.getOrderNo());
            }
        }

        if(CollectionUtils.isNotEmpty(comOrderNoList)){
            throw new RuntimeException("订单号:["+JSON.toJSONString(comOrderNoList)+"]属于非完成状态，无法导入");
        }

        return chargeImportList;
    }

    private BigDecimal toDecimal(String s){
        if(StringUtils.isEmpty(s)){
            return BigDecimal.ZERO;
        }
        return new BigDecimal(s);
    }
}
